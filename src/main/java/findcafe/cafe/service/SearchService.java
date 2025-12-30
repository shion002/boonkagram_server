package findcafe.cafe.service;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.searchdto.CafeNameAddressDto;
import findcafe.cafe.dto.searchdto.CafeSearchResponseDto;
import findcafe.cafe.repository.FilteredCafeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final FilteredCafeRepository filteredCafeRepository;

    public CafeSearchResponseDto cafeSearch(String search){
        List<CafeNameAddressDto> nameList = filteredCafeRepository.findTop10NameWithAddressByNameContaining(search);

        return new CafeSearchResponseDto(nameList);
    }

    public Page<FilteredCafeResponseDto> searchDescendResult(int page, int size, String search) {
        return filteredCafeRepository.searchDescendFilterCafe(page, size, search);
    }

    public Page<FilteredCafeResponseDto> searchNearbyCafes(Double lat, Double lon, int page, int size) {
        return filteredCafeRepository.findNearbyCafes(lat, lon, page, size);
    }
}
