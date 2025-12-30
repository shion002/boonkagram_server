package findcafe.cafe.repository;

import findcafe.cafe.entity.Member;
import findcafe.cafe.entity.PostCafe;
import findcafe.cafe.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
}
