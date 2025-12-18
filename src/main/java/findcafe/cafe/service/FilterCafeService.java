package findcafe.cafe.service;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.entity.FilteredCafe;
import findcafe.cafe.mapper.FilteredCafeMapper;
import findcafe.cafe.repository.FilteredCafeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterCafeService {
    private final FilteredCafeRepository filteredCafeRepository;
    private final EntityManager em;

    @Transactional
    public void updateAllData() {
        filteredCafeRepository.updateCreateData(LocalDateTime.now());
        log.info("업데이트 완료");
        em.clear();
    }

    public Page<FilteredCafeResponseDto> dateDescendFilterCafeList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createData").descending());
        Page<FilteredCafe> cafeList = filteredCafeRepository.findAll(pageable);
        return cafeList.map(FilteredCafeMapper::toDto);
    }
}













