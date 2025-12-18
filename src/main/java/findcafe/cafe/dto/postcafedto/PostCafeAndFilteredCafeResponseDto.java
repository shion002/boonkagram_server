package findcafe.cafe.dto.postcafedto;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.presigneddto.PresignedUrlResponse;
import findcafe.cafe.entity.PostCafeMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCafeAndFilteredCafeResponseDto {
    private String cafeName;
    private String address;
    private String phone;
    private String instagram;
    private String webUrl;
    private String intro;
    private String thumbnail;
    private List<PostCafeMenu> menus;
    private List<String> imageUrls;
    private Double lat;
    private Double lon;
    private String titleIntro;
    private PresignedUrlResponse thumbnailPresignedUrl;
    private List<PresignedUrlResponse> imagePresignedUrls;

    public PostCafeAndFilteredCafeResponseDto (PostCafeResponseDto postCafeResponseDto, FilteredCafeResponseDto filteredCafeResponseDto) {
        this.cafeName = filteredCafeResponseDto.getName();
        this.lat = filteredCafeResponseDto.getLat();
        this.lon = filteredCafeResponseDto.getLon();
        this.titleIntro = filteredCafeResponseDto.getIntro();
        this.address = postCafeResponseDto.getAddress();
        this.phone = postCafeResponseDto.getPhone();
        this.instagram = postCafeResponseDto.getInstagram();
        this.webUrl = postCafeResponseDto.getWebUrl();
        this.intro = postCafeResponseDto.getIntro();
        this.menus = postCafeResponseDto.getMenus();
        this.imageUrls = postCafeResponseDto.getImageUrls();
        this.thumbnail = filteredCafeResponseDto.getThumbnail();
    }

    public PostCafeAndFilteredCafeResponseDto (PostCafeResponseDto postCafeResponseDto, FilteredCafeResponseDto filteredCafeResponseDto,
                                               PresignedUrlResponse thumbnailPresignedUrl, List<PresignedUrlResponse> imagePresignedUrls) {
        this.cafeName = filteredCafeResponseDto.getName();
        this.lat = filteredCafeResponseDto.getLat();
        this.lon = filteredCafeResponseDto.getLon();
        this.titleIntro = filteredCafeResponseDto.getIntro();
        this.address = postCafeResponseDto.getAddress();
        this.phone = postCafeResponseDto.getPhone();
        this.instagram = postCafeResponseDto.getInstagram();
        this.webUrl = postCafeResponseDto.getWebUrl();
        this.intro = postCafeResponseDto.getIntro();
        this.menus = postCafeResponseDto.getMenus();
        this.imageUrls = postCafeResponseDto.getImageUrls();
        this.thumbnailPresignedUrl = thumbnailPresignedUrl;
        this.imagePresignedUrls = imagePresignedUrls;
    }
}
