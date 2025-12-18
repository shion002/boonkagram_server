package findcafe.cafe.dto.cafedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CafeResponseDto {
    private String name;
    private String address;
    private String roadAddress;
    private String industryCode;
    private String industryName;
    private Double lat;
    private Double lon;
}
