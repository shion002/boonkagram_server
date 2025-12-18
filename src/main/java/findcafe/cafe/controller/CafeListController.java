package findcafe.cafe.controller;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.service.FilterCafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/list")
@RequiredArgsConstructor
public class CafeListController {

    private final FilterCafeService filterCafeService;

    @GetMapping("/descend")
    public ResponseEntity<Page<FilteredCafeResponseDto>> descendList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size
    ) {
        Page<FilteredCafeResponseDto> cafeList = filterCafeService.dateDescendFilterCafeList(page, size);
        return ResponseEntity.ok(cafeList);
    }
}
