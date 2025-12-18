package findcafe.cafe.dto.postcafedto;

import findcafe.cafe.dto.presigneddto.PresignedUrlResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long cafeId;
    private PresignedUrlResponse thumbnailPresignedUrl;
    private List<PresignedUrlResponse> imagePresignedUrls;
}
