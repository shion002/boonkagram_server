package findcafe.cafe.dto.reviewdto;

import findcafe.cafe.dto.presigneddto.PresignedUrlResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPresignedResponseDto {

    private List<PresignedUrlResponse> imagePresignedUrls;
}
