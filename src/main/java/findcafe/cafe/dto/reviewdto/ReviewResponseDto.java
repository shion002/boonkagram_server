package findcafe.cafe.dto.reviewdto;

import findcafe.cafe.entity.ReviewScore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String content;
    private List<String> reviewImages;
    private ReviewScore reviewScore;
    private Double ratingScore;
    private String nickname;
    private String profileImage;
    private LocalDateTime createDate;
    private Integer totalReview;

    public ReviewResponseDto(Long id, String content, List<String> reviewImages, ReviewScore reviewScore,
                             Double ratingScore, String nickname, String profileImage, LocalDateTime createDate) {
        this.id = id;
        this.content = content;
        this.reviewImages = reviewImages;
        this.reviewScore = reviewScore;
        this.ratingScore = ratingScore;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.createDate = createDate;
    }
}
