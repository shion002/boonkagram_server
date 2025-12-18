package findcafe.cafe.dto.filteredcafedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilteredCafeRequestDto {

    private String name;
    private String address;
    private String roadAddress;
    private String industryCode;
    private String industryName;
    private Double lat;
    private Double lon;

    private List<String> keywords;
    private String thumbnail;
    private String intro;

    private String source;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createData;

    public FilteredCafeRequestDto(String name, String address, String roadAddress, String industryCode,
                                  String industryName, Double lat, Double lon, List<String> keywords, String source,
                                  Double rating, Integer reviewCount,
                                  LocalDateTime createData, String thumbnail, String intro) {
        this.name = name;
        this.address = address;
        this.roadAddress = roadAddress;
        this.industryCode = industryCode;
        this.industryName = industryName;
        this.lat = lat;
        this.lon = lon;
        this.keywords = keywords;
        this.source = source;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.createData = createData;
        this.thumbnail = thumbnail;
        this.intro = intro;
    }
}
