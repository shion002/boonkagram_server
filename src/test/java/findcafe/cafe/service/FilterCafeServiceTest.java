package findcafe.cafe.service;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.repository.FilteredCafeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class FilterCafeServiceTest {

    @Autowired
    private FilterCafeService filterCafeService;

    @Autowired
    private FilteredCafeRepository filteredCafeRepository;

    @Test
    void dataDescendSort() {

        Page<FilteredCafeResponseDto> filteredCafes = filterCafeService.dateDescendFilterCafeList(0, 27);

        assertThat(filteredCafes).isNotNull();

        filteredCafes.getContent().forEach(cafe ->
                log.info("Cafe: {}, CreateData: {}", cafe.getName(), cafe.getCreateData())
        );
    }

}