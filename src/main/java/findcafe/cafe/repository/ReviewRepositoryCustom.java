package findcafe.cafe.repository;

import findcafe.cafe.dto.reviewdto.ReviewStatsDto;
import findcafe.cafe.entity.Member;
import findcafe.cafe.entity.PostCafe;
import findcafe.cafe.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ReviewRepositoryCustom {

    Page<Review> findByPostCafeIdWithMember(Long cafeId, Pageable pageable);

    long countTodayReviews(Member member, LocalDateTime startOfDay);

    boolean existsByMemberAndPostCafe(Member member, PostCafe postCafe);

    ReviewStatsDto getReviewStatsByFilteredCafeId(Long filteredCafeId);
}
