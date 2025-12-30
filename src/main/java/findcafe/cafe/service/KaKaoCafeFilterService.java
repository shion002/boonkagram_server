package findcafe.cafe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import findcafe.cafe.dto.cafedto.CafeResponseDto;
import findcafe.cafe.dto.filteredcafedto.FilteredCafeRequestDto;
import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.mapper.CafeMapper;
import findcafe.cafe.mapper.FilteredCafeMapper;
import findcafe.cafe.repository.CafeRepository;
import findcafe.cafe.repository.FilteredCafeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KaKaoCafeFilterService {

    /**
     * 크롤링 웹 참조 코드
     */

    private final CafeRepository cafeRepository;
    private final FilteredCafeRepository filteredCafeRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kakao.finder.api.key}")
    private String kakaoApiKey;

    private static final List<String> KEYWORDS = List.of(
            "분위기", "감성", "데이트", "예쁜", "조명", "따뜻", "귀여운", "세련"
    );

    @Async("taskExecutor")
    public void filterByKaKaoReviews() {
        List<CafeResponseDto> cafes = cafeRepository.findAll().stream()
                .map(CafeMapper::toDto)
                .toList();

        for (CafeResponseDto cafe : cafes) {
            try {
                processSingleCafe(cafe);
                Thread.sleep(300); // API 호출 간격
            } catch (HttpClientErrorException.TooManyRequests e) {
                log.error("요청 한도 초과 - 중단합니다.");
                return;
            } catch (Exception e) {
                log.error("[{}] 처리 실패: {}", cafe.getName(), e.getMessage());
            }
        }
    }

    private void processSingleCafe(CafeResponseDto cafe) throws Exception {
        // 이미 필터링된 카페인지 확인
        if (filteredCafeRepository.existsByNameAndAddress(cafe.getName(), cafe.getAddress())) {
            log.info("[{}] 이미 필터링된 카페입니다. 스킵합니다.", cafe.getName());
            return;
        }

        // 카카오 로컬 API로 placeId 검색
        String placeId = searchPlaceId(cafe);
        if (placeId == null) {
            log.warn("[{}] 카카오맵에서 장소를 찾을 수 없습니다.", cafe.getName());
            return;
        }

        // 리뷰 API로 데이터 가져오기
        ReviewData reviewData = fetchReviews(placeId, cafe.getName());
        if (reviewData == null) {
            return;
        }

        // 키워드 매칭된 카페만 저장
        if (!reviewData.matchedKeywords.isEmpty()) {
            FilteredCafeRequestDto dto = new FilteredCafeRequestDto(
                    cafe.getName(),
                    cafe.getAddress(),
                    cafe.getRoadAddress(),
                    cafe.getIndustryCode(),
                    cafe.getIndustryName(),
                    cafe.getLat(),
                    cafe.getLon(),
                    new ArrayList<>(reviewData.matchedKeywords),
                    "카카오맵",
                    reviewData.rating,
                    reviewData.reviewCount,
                    LocalDateTime.now(),
                    null,
                    null
            );

            filteredCafeRepository.save(FilteredCafeMapper.toEntity(dto));
            log.info("[{}] 필터링 완료 - 키워드: {}, 평점: {}, 리뷰수: {}",
                    cafe.getName(), reviewData.matchedKeywords, reviewData.rating, reviewData.reviewCount);
        } else {
            log.info("[{}] 키워드 매칭 없음 (평점: {}, 리뷰수: {})",
                    cafe.getName(), reviewData.rating, reviewData.reviewCount);
        }
    }

    /**
     * 카카오 로컬 API로 장소 ID 검색
     */
    private String searchPlaceId(CafeResponseDto cafe) throws Exception {
        String query = buildSearchQuery(cafe);
        String url = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query", query)
                .queryParam("size", 3)
                .build(false)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JsonNode documents = objectMapper.readTree(response.getBody()).path("documents");

        if (documents.isEmpty()) {
            return null;
        }

        String placeUrl = documents.get(0).path("place_url").asText();
        return placeUrl.replaceAll("\\D+", "");
    }

    /**
     * 카카오맵 리뷰 API로 리뷰 데이터 가져오기
     */
    private ReviewData fetchReviews(String placeId, String cafeName) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://place-api.map.kakao.com/places/tab/reviews/kakaomap/" + placeId)
                .queryParam("order", "RECOMMENDED")
                .queryParam("only_photo_review", "false")
                .build()
                .toUriString();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36");
            headers.add("Accept", "application/json, text/plain, */*");
            headers.add("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            headers.add("Referer", "https://place.map.kakao.com/");
            headers.add("Origin", "https://place.map.kakao.com");
            headers.add("appversion", "6.6.0");
            headers.add("pf", "web");
            headers.add("sec-ch-ua", "\"Chromium\";v=\"142\", \"Google Chrome\";v=\"142\", \"Not_A Brand\";v=\"99\"");
            headers.add("sec-ch-ua-mobile", "?0");
            headers.add("sec-ch-ua-platform", "\"Windows\"");
            headers.add("sec-fetch-dest", "empty");
            headers.add("sec-fetch-mode", "cors");
            headers.add("sec-fetch-site", "same-site");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseReviewData(response.getBody(), cafeName);
            }
        } catch (Exception e) {
            log.warn("[{}] 리뷰 API 호출 실패: {}", cafeName, e.getMessage());
        }

        return null;
    }

    /**
     * 리뷰 JSON 파싱 및 키워드 추출
     */
    private ReviewData parseReviewData(String json, String cafeName) throws Exception {
        JsonNode root = objectMapper.readTree(json);

        // 평점 및 리뷰수 추출
        double rating = root.path("basicInfo").path("feedback").path("scoresum").asDouble(0);
        int reviewCount = root.path("basicInfo").path("feedback").path("scorecnt").asInt(0);

        // 리뷰 리스트에서 키워드 추출
        Set<String> matchedKeywords = new HashSet<>();
        JsonNode reviews = root.path("reviews");

        if (reviews.isArray()) {
            for (JsonNode review : reviews) {
                String contents = review.path("contents").asText();

                for (String keyword : KEYWORDS) {
                    if (contents.contains(keyword)) {
                        matchedKeywords.add(keyword);
                    }
                }
            }
        }

        log.info("[{}] 리뷰 {}개 분석 완료, 매칭 키워드: {}",
                cafeName, reviews.size(), matchedKeywords);

        return new ReviewData(rating, reviewCount, matchedKeywords);
    }

    /**
     * 검색어 생성
     */
    private String buildSearchQuery(CafeResponseDto cafe) {
        StringBuilder query = new StringBuilder("카페 ").append(cafe.getName());

        if (cafe.getRoadAddress() != null && !cafe.getRoadAddress().isBlank()) {
            String[] parts = cafe.getRoadAddress().split(" ");
            for (String part : parts) {
                if (part.endsWith("로") || part.endsWith("길")) {
                    query.append(" ").append(part);
                    break;
                }
            }
        }

        String result = query.toString();
        if (result.length() > 90) {
            result = result.substring(0, 90);
        }

        return result;
    }

    /**
     * 리뷰 데이터 DTO
     */
    private static class ReviewData {
        double rating;
        int reviewCount;
        Set<String> matchedKeywords;

        ReviewData(double rating, int reviewCount, Set<String> matchedKeywords) {
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.matchedKeywords = matchedKeywords;
        }
    }
}