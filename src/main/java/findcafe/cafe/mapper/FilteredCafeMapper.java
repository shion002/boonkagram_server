package findcafe.cafe.mapper;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeRequestDto;
import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.postcafedto.PostCafeRequestDto;
import findcafe.cafe.entity.FilteredCafe;

public class FilteredCafeMapper {

    public static FilteredCafe toEntity(FilteredCafeRequestDto filteredCafeDto){
        return new FilteredCafe(filteredCafeDto.getName(), filteredCafeDto.getAddress(), filteredCafeDto.getRoadAddress(),
                filteredCafeDto.getIndustryCode(), filteredCafeDto.getIndustryName(),
                filteredCafeDto.getLat(), filteredCafeDto.getLon(),
                filteredCafeDto.getKeywords(), filteredCafeDto.getSource(),
                filteredCafeDto.getRating(), filteredCafeDto.getReviewCount(),
                filteredCafeDto.getCreateData(), filteredCafeDto.getThumbnail(), filteredCafeDto.getIntro());
    }

    public static FilteredCafeResponseDto toDto(FilteredCafe filteredCafe){
        return new FilteredCafeResponseDto(filteredCafe.getId(), filteredCafe.getName(), filteredCafe.getAddress(), filteredCafe.getRoadAddress(),
                filteredCafe.getIndustryCode(), filteredCafe.getIndustryName(),
                filteredCafe.getLat(), filteredCafe.getLon(),
                 filteredCafe.getKeywords(), filteredCafe.getSource(), filteredCafe.getRating(),
                filteredCafe.getReviewCount(), filteredCafe.getCreateData(),
                filteredCafe.getThumbnail(), filteredCafe.getTitleIntro(), filteredCafe.getS3FolderPath());
    }
}
