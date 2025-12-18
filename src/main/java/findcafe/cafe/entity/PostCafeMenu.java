package findcafe.cafe.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCafeMenu {
    private String name;
    private Integer price;

    public PostCafeMenu(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
}
