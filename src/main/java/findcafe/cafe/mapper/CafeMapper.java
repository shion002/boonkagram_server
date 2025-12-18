package findcafe.cafe.mapper;

import findcafe.cafe.dto.cafedto.CafeRequestDto;
import findcafe.cafe.dto.cafedto.CafeResponseDto;
import findcafe.cafe.entity.Cafe;

public class CafeMapper {
    public static Cafe toEntity(CafeRequestDto cafeRequestDto){
        return new Cafe(cafeRequestDto.getName(), cafeRequestDto.getAddress(), cafeRequestDto.getRoadAddress(),
                cafeRequestDto.getIndustryCode(), cafeRequestDto.getIndustryName(),
                parseDoubleOrNull(cafeRequestDto.getLat()), parseDoubleOrNull(cafeRequestDto.getLon()));
    }
    private static Double parseDoubleOrNull(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static CafeResponseDto toDto(Cafe cafe) {
        return new CafeResponseDto(cafe.getName(), cafe.getAddress(),
                cafe.getRoadAddress(), cafe.getIndustryCode(), cafe.getIndustryName(),
                cafe.getLat(), cafe.getLon());
    }

    private static String parseStringOrNull(Double value) {
        if (value == null) return null;
        return String.valueOf(value);
    }
}
