package findcafe.cafe.mapper;

import findcafe.cafe.dto.reviewdto.ReviewRequestDto;
import findcafe.cafe.dto.reviewdto.ReviewResponseDto;
import findcafe.cafe.entity.Member;
import findcafe.cafe.entity.PostCafe;
import findcafe.cafe.entity.Review;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class ReviewMapper {
    public static Review toEntity (ReviewRequestDto reviewRequestDto, Member member,
                                   PostCafe postCafe, List<String> imageUrls, String s3FolderPath) {
        return new Review(reviewRequestDto.getContent(), imageUrls,
                reviewRequestDto.getReviewScore(), member, postCafe, s3FolderPath);
    }

    public static ReviewResponseDto toDto (Review review) {

        return new ReviewResponseDto(review.getId(), review.getContent(), review.getReviewImages(), review.getReviewScore(),
                review.getRatingScore(), review.getMember().getNickname(), review.getMember().getProfileImageUrl(),
                review.getCreateDate());
    }

    public static ReviewResponseDto toDto (Review review, Map<Long, Long> reviewCountMap) {
        Long memberId = review.getMember().getId();
        Long memberTotalReview = reviewCountMap.getOrDefault(memberId, 0L);

        return new ReviewResponseDto(review.getId(), review.getContent(), review.getReviewImages(), review.getReviewScore(),
                review.getRatingScore(), review.getMember().getNickname(), review.getMember().getProfileImageUrl(),
                review.getCreateDate(), memberTotalReview.intValue());
    }

}
