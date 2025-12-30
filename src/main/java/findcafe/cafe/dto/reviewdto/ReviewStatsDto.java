package findcafe.cafe.dto.reviewdto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ReviewStatsDto {
    private final Double averageRating;
    private final Long reviewCount;

    @QueryProjection
    public ReviewStatsDto(Double averageRating, Long reviewCount) {
        this.averageRating = averageRating != null ? averageRating : 0.0;
        this.reviewCount = reviewCount != null ? reviewCount : 0L;
    }

    public Integer getReviewCountAsInt() {
        return reviewCount.intValue();
    }
}
