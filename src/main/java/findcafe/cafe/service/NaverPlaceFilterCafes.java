package findcafe.cafe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import findcafe.cafe.dto.cafedto.CafeResponseDto;
import findcafe.cafe.mapper.CafeMapper;
import findcafe.cafe.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverPlaceFilterCafes {

    private final CafeRepository cafeRepository;
    private final RestTemplate restTemplate = new RestTemplate();


    @Value("${naver.finder.api.client-id}")
    private String clientId;

    @Value("${naver.finder.api.client-secret}")
    private String clientSecret;

    @Async("taskExecutor")
    public CompletableFuture<Void> filterRegisterCafes() throws InterruptedException {
        List<CafeResponseDto> cafes = cafeRepository.findAllAfterId(25000L)
                        .stream().map(CafeMapper::toDto).toList();
        log.info("총 {}개 카페 네이버 플레이스 대조 시작", cafes.size());

        AtomicBoolean stopFlag = new AtomicBoolean(false);

        for (CafeResponseDto cafeDto : cafes) {
            if (stopFlag.get()) {
                log.warn("429 한도 초과로 작업 중단됨 — 남은 {}개는 생략", cafes.size());
                break;
            }


            try {
                processCafeFilter(cafeDto);
            } catch (HttpClientErrorException.TooManyRequests e) {
                log.warn("429 TooManyRequests 발생 — 전체 중단 플래그 활성화");
                stopFlag.set(true);
            } catch (Exception e) {
                log.error("카페 처리 중 오류: {} - {}", cafeDto.getName(), e.getMessage());
            }

            Thread.sleep(150);
        }

        return CompletableFuture.completedFuture(null);
    }

    private void processCafeFilter(CafeResponseDto cafeDto) {
        try {
            boolean isRegistered = checkNaverPlaceRegistered(cafeDto.getName(), cafeDto.getRoadAddress());

            if(isRegistered) {
                log.info("등록 카페 : {}", cafeDto.getName());
            } else {
                log.info("미등록 카페 제외 : {}", cafeDto.getName());

                cafeRepository.findByNameAndRoadAddress(cafeDto.getName(), cafeDto.getRoadAddress())
                        .ifPresent(existing -> {
                            cafeRepository.delete(existing);
                            log.info("폐업으로 삭제된 카페: {}", cafeDto.getName());
                        });

            }
        }
            catch (Exception e){
            log.error("카페 처리 중 오류 발생: {} - {}", cafeDto.getName(), e.getMessage());
        }
    }


    private boolean checkNaverPlaceRegistered(String name, String roadAddress) {
        try {
            String queryText = "카페 " + name;

            if (roadAddress != null && !roadAddress.isBlank()) {
                String[] parts = roadAddress.split(" ");
                if (parts.length >= 3) {
                    String siGu = parts[0] + " " + parts[1];
                    String road = parts[2];
                    queryText += " " + siGu + " " + road;
                }
            }

            String url = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/search/local.json")
                    .queryParam("query", queryText)
                    .queryParam("display", 3)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Naver-Client-Id", clientId);
            headers.add("X-Naver-Client-Secret", clientSecret);
            headers.add("Accept", "application/json");
            headers.add("User-Agent", "Mozilla/5.0");

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            JsonNode root = new ObjectMapper().readTree(response.getBody());
            JsonNode items = root.path("items");

            if (items == null || !items.isArray() || items.size() == 0) {
                log.info("[{}] 검색 결과 없음", name);
                return false;
            }

            for (JsonNode item : items) {
                try {
                    String category = item.path("category").asText("");
                    if (category != null && category.contains("카페")) {
                        return true;
                    }
                } catch (Exception ex) {
                    log.warn("[{}] 아이템 처리 실패: {}", name, ex.getMessage());
                }
            }

            return false;

        } catch (HttpClientErrorException.TooManyRequests e) {
            throw e;
        } catch (Exception e){
            log.error("네이버 API 호출 실패: {} - {}", name, e.getMessage());
            return false;
        }
    }
}

























