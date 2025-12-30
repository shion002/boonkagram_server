package findcafe.cafe.service;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.reviewdto.ReviewStatsDto;
import findcafe.cafe.entity.FilteredCafe;
import findcafe.cafe.mapper.FilteredCafeMapper;
import findcafe.cafe.repository.FilteredCafeRepository;
import findcafe.cafe.repository.ReviewRepository;
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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterCafeService {
    private final FilteredCafeRepository filteredCafeRepository;
    private final EntityManager em;
    private final ReviewRepository reviewRepository;

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

    @Transactional
    public void updateFilteredCafeReviewStats(Long filteredCafeId) {
        FilteredCafe filteredCafe = filteredCafeRepository.findById(filteredCafeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 카페입니다"));

        ReviewStatsDto stats = reviewRepository.getReviewStatsByFilteredCafeId(filteredCafeId);

        filteredCafe.updateReviewStats(stats.getAverageRating(), stats.getReviewCountAsInt());
    }
}













