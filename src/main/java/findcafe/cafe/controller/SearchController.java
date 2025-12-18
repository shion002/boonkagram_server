package findcafe.cafe.controller;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.searchdto.CafeSearchResponseDto;
import findcafe.cafe.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("cafe-search")
    public ResponseEntity<CafeSearchResponseDto> postSearch(@RequestParam String search){

        CafeSearchResponseDto cafeSearch = searchService.cafeSearch(search);

        return ResponseEntity.ok(cafeSearch);
    }

    @GetMapping("list-search-descend")
    public ResponseEntity<Page<FilteredCafeResponseDto>> listSearchDescend(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam String search) {
        Page<FilteredCafeResponseDto> filteredCafeResponseDtos = searchService.searchDescendResult(page, size, search);
        return ResponseEntity.ok(filteredCafeResponseDtos);
    }

    @GetMapping("nearby")
    public ResponseEntity<Page<FilteredCafeResponseDto>> listNearby(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size
    ) {
        return ResponseEntity.ok(searchService.searchNearbyCafes(lat, lon, page, size));
    }
}
