package findcafe.cafe.repository;

import findcafe.cafe.entity.PostCafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCafeRepository extends JpaRepository<PostCafe, Long> {
    Optional<PostCafe> findByFilteredCafeId(Long filteredCafeId);
}
