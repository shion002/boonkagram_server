package findcafe.cafe.repository;

import findcafe.cafe.entity.FilteredCafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilteredCafeRepository extends JpaRepository<FilteredCafe, Long>, FilteredCafeRepositoryCustom {
    boolean existsByNameAndAddress(String name, String address);

}
