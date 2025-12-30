package findcafe.cafe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String content;

    @Column(name = "review_image_url", length = 500)
    @OrderColumn(name = "review_image_order")
    private List<String> reviewImages = new ArrayList<>();

    @Embedded
    private ReviewScore reviewScore;

    private LocalDateTime createDate;
    private Double ratingScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_cafe_id")
    private PostCafe postCafe;

    private String s3FolderPath;


    public Review(String content, List<String> reviewImages, ReviewScore reviewScore, Member member, PostCafe postCafe, String s3FolderPath) {
        this.content = content;
        this.reviewImages = reviewImages;
        this.reviewScore = reviewScore;
        this.member = member;
        this.postCafe = postCafe;
        this.createDate = LocalDateTime.now();
        this.s3FolderPath = s3FolderPath;
        this.ratingScore = ((reviewScore.getCostScore() + reviewScore.getServiceScore() +
                reviewScore.getMoodScore() + reviewScore.getTasteScore()) / 4.0);
    }
}



