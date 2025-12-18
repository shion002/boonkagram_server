package findcafe.cafe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Cafe {

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

    public Cafe(String name, String address, String roadAddress, String industryCode, String industryName, Double lat, Double lon) {
        this.name = name;
        this.address = address;
        this.roadAddress = roadAddress;
        this.industryCode = industryCode;
        this.industryName = industryName;
        this.lat = lat;
        this.lon = lon;
    }
}
