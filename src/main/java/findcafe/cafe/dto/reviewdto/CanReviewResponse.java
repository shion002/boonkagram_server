package findcafe.cafe.dto.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanReviewResponse {
    private boolean canReview;
    private String message;
}
