package findcafe.cafe.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ReviewScore {

    private Integer tasteScore;
    private Integer serviceScore;
    private Integer moodScore;
    private Integer costScore;

    public ReviewScore(Integer tasteScore, Integer serviceScore, Integer moodScore, Integer costScore) {
        this.tasteScore = tasteScore;
        this.serviceScore = serviceScore;
        this.moodScore = moodScore;
        this.costScore = costScore;
    }
}

