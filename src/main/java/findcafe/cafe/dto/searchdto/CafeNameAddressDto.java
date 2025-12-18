package findcafe.cafe.dto.searchdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CafeNameAddressDto {

    private Long filteredCafeId;
    private String name;
    private String address;
}
