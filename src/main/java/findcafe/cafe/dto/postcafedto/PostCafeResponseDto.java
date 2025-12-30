package findcafe.cafe.dto.postcafedto;

import findcafe.cafe.dto.reviewdto.ReviewResponseDto;
import findcafe.cafe.entity.PostCafeMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCafeResponseDto {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String instagram;
    private String webUrl;
    private String intro;
    private List<PostCafeMenu> menus;
    private List<String> imageUrls;

    public PostCafeResponseDto(String name, String address, String phone, String instagram, String webUrl, String intro, List<PostCafeMenu> menus, List<String> imageUrls) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.instagram = instagram;
        this.webUrl = webUrl;
        this.intro = intro;
        this.menus = menus;
        this.imageUrls = imageUrls;
    }
}
