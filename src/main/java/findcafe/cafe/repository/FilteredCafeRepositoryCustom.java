package findcafe.cafe.repository;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.searchdto.CafeNameAddressDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface FilteredCafeRepositoryCustom {
    void updateCreateData(LocalDateTime dateTime);

    List<CafeNameAddressDto> findTop10NameWithAddressByNameContaining(String search);

    Page<FilteredCafeResponseDto> searchDescendFilterCafe(int page, int size, String search);

    Page<FilteredCafeResponseDto> findNearbyCafes(Double lat, Double lon, int page, int size);

}
