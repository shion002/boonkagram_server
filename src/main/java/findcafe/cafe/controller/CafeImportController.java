package findcafe.cafe.controller;

import findcafe.cafe.service.CafeImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cafes")
public class CafeImportController {

    private final CafeImportService cafeImportService;

    public ResponseEntity<String> importCafes() {
        cafeImportService.importCafesAsync();
        return ResponseEntity.ok("카페 데이터 수집이 백그라운드에서 시작되었습니다.");
    }

}
