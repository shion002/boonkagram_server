package findcafe.cafe.dto.reviewdto;

import findcafe.cafe.dto.utildto.ImageDto;
import findcafe.cafe.entity.ReviewScore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {
    private String content;
    private List<ImageDto> reviewImages;
    private ReviewScore reviewScore;
    private Long postId;
}
