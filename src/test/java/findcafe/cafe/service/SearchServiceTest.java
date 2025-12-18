package findcafe.cafe.service;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.searchdto.CafeNameAddressDto;
import findcafe.cafe.dto.searchdto.CafeSearchResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@Transactional
class SearchServiceTest {

    @Autowired private SearchService searchService;

    @Test
    void search(){
        CafeSearchResponseDto cafeSearch = searchService.cafeSearch("대전");
        List<CafeNameAddressDto> nameResults = cafeSearch.getNameResults();
        for (CafeNameAddressDto nameResult : nameResults) {
            log.info("카페아이디: {}", nameResult.getFilteredCafeId());
            log.info("카페이름: {}", nameResult.getName());
            log.info("카페 이름에 대한 주소: {}", nameResult.getAddress());
        }
    }

    @Test
    void listSearch() {
        Page<FilteredCafeResponseDto> filteredCafeResponseDtos = searchService.searchDescendResult(0, 24, "대전");
        for (FilteredCafeResponseDto filteredCafeResponseDto : filteredCafeResponseDtos) {
            log.info("아이디 {}", filteredCafeResponseDto.getId());
            log.info("이름 {}", filteredCafeResponseDto.getName());
            log.info("주소 {}", filteredCafeResponseDto.getAddress());
            log.info("소개 {}", filteredCafeResponseDto.getTitleIntro());
            log.info("위도 {}", filteredCafeResponseDto.getLat());
            log.info("경도 {}", filteredCafeResponseDto.getLon());
            log.info("썸네일 {}", filteredCafeResponseDto.getThumbnail());
        }
    }
}