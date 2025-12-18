package findcafe.cafe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import findcafe.cafe.dto.cafedto.CafeRequestDto;
import findcafe.cafe.entity.Cafe;
import findcafe.cafe.mapper.CafeMapper;
import findcafe.cafe.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeImportService {

    private final CafeRepository cafeRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openapi.url}")
    private String baseUrl;

    @Value("${openapi.serviceKey}")
    private String serviceKey;

    @Async
    public CompletableFuture<Void> importCafesAsync() {
        int page = 1;
        int perPage = 1000;

        while (true) {
            try {
                String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
                String url = String.format(
                        "%s/storeListInUpjong?serviceKey=%s&divId=indsSclsCd&key=I21201&indsLclsCd=I2&indsMclsCd=I21&indsSclsCd=I21201&pageNo=%d&numOfRows=%d&type=json",
                        baseUrl, encodedKey, page, perPage
                );

                log.info("요청 URL: {}", url);
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());

                JsonNode items = root.path("body").path("items");
                if (items.isMissingNode() || !items.isArray() || items.isEmpty()) {
                    log.info("데이터 수집 완료 (마지막 페이지: {})", page);
                    break;
                }

                List<Cafe> cafes = new ArrayList<>();
                for (JsonNode node : items) {
                    Cafe cafe = CafeMapper.toEntity(new CafeRequestDto(
                            node.path("bizesNm").asText(),
                            node.path("lnoAdr").asText(""),
                            node.path("rdnmAdr").asText(""),
                            node.path("indsSclsCd").asText(""),
                            node.path("indsSclsNm").asText(""),
                            node.path("lat").asText(""),
                            node.path("lon").asText("")
                    ));
                    cafes.add(cafe);
                }

                cafeRepository.saveAll(cafes);
                log.info("Page {} 저장 완료 ({}건)", page, cafes.size());

                page++;
                Thread.sleep(300);

            } catch (Exception e) {
                log.error("Page {} 처리 중 오류: {}", page, e.getMessage());
                break;
            }
        }

        return CompletableFuture.completedFuture(null);
    }

}
