package findcafe.cafe.repository;

import findcafe.cafe.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    Optional<Cafe> findByNameAndRoadAddress(String name, String address);

    @Query("SELECT c FROM Cafe c WHERE c.id > :startId ORDER BY c.id ASC")
    List<Cafe> findAllAfterId(@Param("startId") Long startId);
}
