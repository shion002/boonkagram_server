package findcafe.cafe.entity;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.postcafedto.PostCafeRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class FilteredCafe {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;
    private String address;
    private String roadAddress;
    private String industryCode;
    private String industryName;
    private Double lat;
    private Double lon;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> keywords = new ArrayList<>(); // 키워드 배열

    @Column(length = 500)
    private String thumbnail;

    private String source;
    private Double rating;
    private Integer reviewCount;
    private String titleIntro;
    private String s3FolderPath;

    private LocalDateTime createData;
    private LocalDateTime updateData;

    public FilteredCafe(String name, String address, String roadAddress, String industryCode, String industryName, Double lat,
                        Double lon, List<String> keywords, String source, Double rating,
                        Integer reviewCount, LocalDateTime createData, String intro, String thumbnail) {
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
        this.titleIntro = intro;
        this.thumbnail = thumbnail;
    }

    public FilteredCafe(PostCafeRequestDto dto, String thumbnail, String s3FolderPath) {
        validateRequiredFields(dto);

        this.name = dto.getCafeName();
        this.address = dto.getAddress();
        this.lat = dto.getLat();
        this.lon = dto.getLon();
        this.titleIntro = dto.getTitleIntro();
        this.thumbnail = thumbnail;
        this.s3FolderPath = s3FolderPath;
        this.createData = LocalDateTime.now();
        this.updateData = LocalDateTime.now();

        this.reviewCount = 0;
        this.rating = 0.0;
    }

    // 메서드

    private void validateRequiredFields(PostCafeRequestDto dto) {
        if (dto.getCafeName() == null || dto.getCafeName().trim().isEmpty()) {
            throw new IllegalArgumentException("카페 이름이 등록되지 않았습니다");
        }
        if (dto.getAddress() == null || dto.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("주소가 등록되지 않았습니다");
        }
        if (dto.getLat() == null || dto.getLon() == null) {
            throw new IllegalArgumentException("위도/경도가 등록되지 않았습니다");
        }
    }

    public FilteredCafeResponseDto update(PostCafeRequestDto dto, String thumbnailUrl) {
        if (dto.getCafeName() != null) {
            this.name = dto.getCafeName();
        }
        if (dto.getAddress() != null) {
            this.address = dto.getAddress();
        }
        this.titleIntro = dto.getTitleIntro();

        if (dto.getLat() != null) {
            this.lat = dto.getLat();
        }
        if (dto.getLon() != null) {
            this.lon = dto.getLon();
        }
        this.thumbnail = thumbnailUrl;
        this.updateData = LocalDateTime.now();

        return new FilteredCafeResponseDto(dto.getCafeName(), dto.getAddress(), dto.getLat(),
                dto.getLon(), thumbnailUrl, dto.getTitleIntro());
    }

    public void updateReviewStats(Double averageRating, Integer count) {
        this.rating = averageRating;
        this.reviewCount = count;
    }

    public void updateS3FolderPath(String s3FolderPath) {
        this.s3FolderPath = s3FolderPath;
    }
}
















