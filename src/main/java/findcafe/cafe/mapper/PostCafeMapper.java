package findcafe.cafe.mapper;

import findcafe.cafe.dto.postcafedto.PostCafeRequestDto;
import findcafe.cafe.dto.postcafedto.PostCafeResponseDto;
import findcafe.cafe.dto.reviewdto.ReviewResponseDto;
import findcafe.cafe.dto.utildto.ImageDto;
import findcafe.cafe.entity.PostCafe;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostCafeMapper {
    public static PostCafe toEntity(PostCafeRequestDto postCafeRequestDto){
        return new PostCafe(postCafeRequestDto.getCafeName(), postCafeRequestDto.getAddress(),
                postCafeRequestDto.getPhone(), postCafeRequestDto.getInstagram(),
                postCafeRequestDto.getWebUrl(), postCafeRequestDto.getFilteredCafeId(),
                postCafeRequestDto.getIntro(), postCafeRequestDto.getMenus(),
                postCafeRequestDto.getImageUrls().stream().map(ImageDto::getFileName).collect(Collectors.toList()));
    }

    public static PostCafeResponseDto toDto(PostCafe postCafe){
        return new PostCafeResponseDto(postCafe.getId(), postCafe.getName(), postCafe.getAddress(), postCafe.getPhone(), postCafe.getInstagram(),
                postCafe.getWebUrl(), postCafe.getIntro(), postCafe.getMenus(), postCafe.getImageUrls());
    }

}
