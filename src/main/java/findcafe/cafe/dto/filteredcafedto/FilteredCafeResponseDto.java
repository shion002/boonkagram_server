package findcafe.cafe.dto.filteredcafedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilteredCafeResponseDto {

    private Long id;
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
    private String s3FolderPath;
    private String titleIntro;

    private Double distance;

    public FilteredCafeResponseDto(String name, String address, Double lat, Double lon, String thumbnail, String titleIntro) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.thumbnail = thumbnail;
        this.titleIntro = titleIntro;
    }
    public FilteredCafeResponseDto(Long id, String name, String address, Double lat, Double lon, String thumbnail, String titleIntro) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.thumbnail = thumbnail;
        this.titleIntro = titleIntro;
    }

    public FilteredCafeResponseDto(Long id, String name, String address, Double lat, Double lon, String thumbnail, String titleIntro, Double distance) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.thumbnail = thumbnail;
        this.titleIntro = titleIntro;
        this.distance = distance;
    }

    private String source;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createData;



    public FilteredCafeResponseDto(Long id, String name, String address, String roadAddress,
                                   String industryCode, String industryName, Double lat, Double lon,
                                   List<String> keywords, String source, Double rating, Integer reviewCount,
                                   LocalDateTime createData, String thumbnail, String titleIntro, String s3FolderPath) {
        this.id = id;
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
        this.titleIntro = titleIntro;
        this.s3FolderPath = s3FolderPath;
    }
}
