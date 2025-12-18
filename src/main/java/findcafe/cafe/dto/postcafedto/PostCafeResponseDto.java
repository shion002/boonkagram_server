package findcafe.cafe.dto.postcafedto;

import findcafe.cafe.entity.PostCafeMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCafeResponseDto {

    private String name;
    private String address;
    private String phone;
    private String instagram;
    private String webUrl;
    private String intro;
    private List<PostCafeMenu> menus;
    private List<String> imageUrls;


}
