package findcafe.cafe.dto.presigneddto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageUpdateRequest {

    private String profileImageUrl;
}
