package findcafe.cafe.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;


@Service
@Slf4j
public class SmsService {

    private final RestTemplate restTemplate;

    @Value("${solapi.api-key}")
    private String apiKey;

    @Value("${solapi.api-secret}")
    private String apiSecret;

    @Value("${solapi.from-number}")
    private String fromNumber;

    private final ConcurrentHashMap<String, VerificationInfo> verificationStore = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, LocalDateTime> lastSentTime = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, LocalDateTime> verifiedPhones = new ConcurrentHashMap<>();


    private static final int COOLDOWN_MINUTES = 1;

    private static final int EXPIRY_MINUTES = 3;

    private static final int VERIFIED_EXPIRY_MINUTES = 10;


    public SmsService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::cleanExpiredData, 30, 30, TimeUnit.SECONDS);
    }

    public void sendVerificationCode(String phoneNumber) {
        LocalDateTime lastSent = lastSentTime.get(phoneNumber);
        if (lastSent != null) {
            LocalDateTime cooldownExpiry = lastSent.plusMinutes(COOLDOWN_MINUTES);
            if (LocalDateTime.now().isBefore(cooldownExpiry)) {
                long secondsRemaining = java.time.Duration.between(LocalDateTime.now(), cooldownExpiry).getSeconds();
                log.warn("인증번호 재발송 제한 - 전화번호: {}, 남은 시간: {}초", phoneNumber, secondsRemaining);
                throw new RuntimeException(String.format("인증번호는 %d초 후에 재요청 가능합니다.", secondsRemaining));
            }
        }
        String verificationCode = generateVerificationCode();
        try {
            sendSms(phoneNumber, String.format("[인증번호] %s를 입력해주세요.", verificationCode));

            VerificationInfo info = new VerificationInfo(verificationCode, LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
            verificationStore.put(phoneNumber, info);

            lastSentTime.put(phoneNumber, LocalDateTime.now());

            log.info("인증번호 발송 완료 - 전화번호: {}", phoneNumber);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("SMS 발송 실패 - 전화번호: {}", phoneNumber, e);
            throw new RuntimeException("SMS 발송 실패", e);
        }
    }

    public boolean verifyCode(String phoneNumber, String code) {
        VerificationInfo info = verificationStore.get(phoneNumber);

        if (info == null) {
            log.warn("인증번호 검증 실패 - 전화번호: {} (인증번호 없음)", phoneNumber);
            return false;
        }

        if (LocalDateTime.now().isAfter(info.getExpiryTime())) {
            verificationStore.remove(phoneNumber);
            log.warn("인증번호 검증 실패 - 전화번호: {} (만료됨)", phoneNumber);
            return false;
        }

        boolean isValid = info.getCode().equals(code);

        if (isValid) {
            verificationStore.remove(phoneNumber);
            lastSentTime.remove(phoneNumber);

            verifiedPhones.put(phoneNumber, LocalDateTime.now());
            log.info("인증번호 검증 성공 - 전화번호: {}", phoneNumber);
        } else {
            log.warn("인증번호 검증 실패 - 전화번호: {} (코드 불일치)", phoneNumber);
        }

        return isValid;
    }


    public boolean isPhoneVerified(String phoneNumber) {
        LocalDateTime verifiedTime = verifiedPhones.get(phoneNumber);

        if (verifiedTime == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(verifiedTime.plusMinutes(VERIFIED_EXPIRY_MINUTES))) {
            verifiedPhones.remove(phoneNumber);
            log.warn("인증 완료 상태 만료 - 전화번호: {}", phoneNumber);
            return false;
        }

        return true;
    }

    public void clearVerifiedStatus(String phoneNumber) {
        verifiedPhones.remove(phoneNumber);
        log.info("인증 완료 상태 제거 - 전화번호: {}", phoneNumber);
    }

    private void cleanExpiredData() {
        LocalDateTime now = LocalDateTime.now();
        int removedVerifications = 0;
        int removedCooldowns = 0;
        int removedVerifiedStatus = 0;

        // 만료된 인증번호 삭제
        for (Map.Entry<String, VerificationInfo> entry : verificationStore.entrySet()) {
            if (now.isAfter(entry.getValue().getExpiryTime())) {
                verificationStore.remove(entry.getKey());
                removedVerifications++;
            }
        }

        // 만료된 쿨타임 삭제
        for (Map.Entry<String, LocalDateTime> entry : lastSentTime.entrySet()) {
            if (now.isAfter(entry.getValue().plusMinutes(COOLDOWN_MINUTES))) {
                lastSentTime.remove(entry.getKey());
                removedCooldowns++;
            }
        }

        // 만료된 인증 완료 상태 삭제
        for (Map.Entry<String, LocalDateTime> entry : verifiedPhones.entrySet()) {
            if (now.isAfter(entry.getValue().plusMinutes(VERIFIED_EXPIRY_MINUTES))) {
                verifiedPhones.remove(entry.getKey());
                removedVerifiedStatus++;
            }
        }

        if (removedVerifications > 0 || removedCooldowns > 0 || removedVerifiedStatus > 0) {
            log.info("만료된 데이터 삭제 - 인증번호: {}개, 쿨타임: {}개, 인증완료: {}개",
                    removedVerifications, removedCooldowns, removedVerifiedStatus);
        }
    }

    private void sendSms(String to, String text) {
        String url = "https://api.solapi.com/messages/v4/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", createAuthorizationHeader());

        Map<String, Object> message = new HashMap<>();
        message.put("to", to);
        message.put("from", fromNumber);
        message.put("text", text);

        Map<String, Object> body = new HashMap<>();
        body.put("message", message);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("SMS 발송 API 호출 실패: " + response.getStatusCode());
        }
    }

    private String createAuthorizationHeader() {
        try {
            String dateTime = Instant.now().toString();
            String salt = UUID.randomUUID().toString().replace("-", "");
            String signature = generateSignature(dateTime, salt);

            return String.format("HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                    apiKey, dateTime, salt, signature);
        } catch (Exception e) {
            throw new RuntimeException("인증 헤더 생성 실패", e);
        }
    }

    private String generateSignature(String dateTime, String salt)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String data = dateTime + salt;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);

        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private String generateVerificationCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }


    @Getter
    private static class VerificationInfo {
        private final String code;
        private final LocalDateTime expiryTime;

        public VerificationInfo(String code, LocalDateTime expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }

    }
}



















