package findcafe.cafe.entity;

import findcafe.cafe.dto.postcafedto.PostCafeRequestDto;
import findcafe.cafe.dto.postcafedto.PostCafeResponseDto;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class PostCafe {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String instagram;
    private String webUrl;

    private Long filteredCafeId;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String intro;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "post_cafe_menu",
            joinColumns = @JoinColumn(name = "post_cafe_id")
    )
    private List<PostCafeMenu> menus = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "post_cafe_image",
            joinColumns = @JoinColumn(name = "post_cafe_id")
    )
    @Column(name = "image_url", length = 500)
    @OrderColumn(name = "image_order")
    private List<String> imageUrls =  new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "postCafe")
    private List<Review> reviews = new ArrayList<>();

    public PostCafe(String name, String address, String phone, String instagram,
                    String webUrl, Long filteredCafeId, String intro, List<PostCafeMenu> menus,
                    List<String> imageUrls) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.instagram = instagram;
        this.webUrl = webUrl;
        this.filteredCafeId = filteredCafeId;
        this.intro = intro;
        this.menus = menus;
        this.imageUrls = imageUrls;
    }

    public PostCafe() {
    }
    // 메서드

    public PostCafeResponseDto setUpdateData(PostCafeRequestDto postCafeRequestDto, List<String> imageUrls){
        return setData(postCafeRequestDto, imageUrls);
    }
    public void setCreateData(PostCafeRequestDto postCafeRequestDto, Long createdFilteredId, List<String> imageUrls){
        if(postCafeRequestDto.getCafeName() == null){
            throw new RuntimeException("카페 이름이 등록되지 않았습니다");
        }
        setData(postCafeRequestDto, imageUrls);
        this.filteredCafeId = createdFilteredId;
    }

    private PostCafeResponseDto setData(PostCafeRequestDto postCafeRequestDto, List<String> imageUrls) {
        if (postCafeRequestDto.getCafeName() != null) {
            this.name = postCafeRequestDto.getCafeName();
        }
        if (postCafeRequestDto.getAddress() != null) {
            this.address = postCafeRequestDto.getAddress();
        }
        this.phone = postCafeRequestDto.getPhone();

        this.instagram = postCafeRequestDto.getInstagram();

        this.webUrl = postCafeRequestDto.getWebUrl();

        this.intro = postCafeRequestDto.getIntro();

        this.menus = postCafeRequestDto.getMenus();

        this.imageUrls = imageUrls;

        return new PostCafeResponseDto(postCafeRequestDto.getCafeName(), postCafeRequestDto.getAddress(),
                postCafeRequestDto.getPhone(), postCafeRequestDto.getInstagram(), postCafeRequestDto.getWebUrl(),
                postCafeRequestDto.getIntro(), postCafeRequestDto.getMenus(), imageUrls);
    }
}
