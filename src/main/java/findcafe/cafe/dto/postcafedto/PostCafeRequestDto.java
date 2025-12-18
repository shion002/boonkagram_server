package findcafe.cafe.dto.postcafedto;

import findcafe.cafe.dto.utildto.ImageDto;
import findcafe.cafe.entity.PostCafeMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCafeRequestDto {
    private String cafeName;
    private String address;
    private String titleIntro;
    private ImageDto thumbnail;
    private Double lat;
    private Double lon;

    private String phone;
    private String instagram;
    private String webUrl;
    private Long filteredCafeId;
    private String intro;
    private List<PostCafeMenu> menus;
    private List<ImageDto> imageUrls;
    private List<String> existingImageUrls;
    private String existingThumbnailUrl;
    private Boolean deleteThumbnail;

    public PostCafeRequestDto(String cafeName, String address, Long filteredCafeId) {
        this.cafeName = cafeName;
        this.address = address;
        this.filteredCafeId = filteredCafeId;
    }

    public PostCafeRequestDto(Long filteredCafeId, String titleIntro, String phone, String instagram, String webUrl,
                              String intro, List<PostCafeMenu> menus, Double lat, Double lon) {
        this.filteredCafeId = filteredCafeId;
        this.titleIntro = titleIntro;
        this.phone = phone;
        this.instagram = instagram;
        this.webUrl = webUrl;
        this.intro = intro;
        this.menus = menus;
        this.lat = lat;
        this.lon = lon;
    }

    public PostCafeRequestDto(String cafeName, String address, String titleIntro, String phone, String instagram, String webUrl,
                              String intro, List<PostCafeMenu> menus, Double lat, Double lon) {
        this.cafeName = cafeName;
        this.address = address;
        this.titleIntro = titleIntro;
        this.phone = phone;
        this.instagram = instagram;
        this.webUrl = webUrl;
        this.intro = intro;
        this.menus = menus;
        this.lat = lat;
        this.lon = lon;
    }
}
