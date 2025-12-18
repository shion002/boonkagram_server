package findcafe.cafe.controller;

import findcafe.cafe.service.CafeImportService;
import findcafe.cafe.service.KaKaoCafeFilterService;
import findcafe.cafe.service.NaverPlaceFilterCafes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cafes")
public class CafeImportController {

    private final CafeImportService cafeImportService;
    private final NaverPlaceFilterCafes naverPlaceFilterCafes;
    private final KaKaoCafeFilterService kaKaoCafeFilterService;

    public ResponseEntity<String> importCafes() {
        cafeImportService.importCafesAsync();
        return ResponseEntity.ok("카페 데이터 수집이 백그라운드에서 시작되었습니다.");
    }

    public ResponseEntity<String> naverFilterCafe() throws InterruptedException {
        naverPlaceFilterCafes.filterRegisterCafes();
        return ResponseEntity.ok("카페 필터링 시작");
    }

    public ResponseEntity<String> filterKakaoCafe(){
        kaKaoCafeFilterService.filterByKaKaoReviews();
        return ResponseEntity.ok("카카오 필터링 비동기 시작");
    }
}
