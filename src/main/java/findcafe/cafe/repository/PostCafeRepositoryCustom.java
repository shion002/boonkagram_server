package findcafe.cafe.repository;

import java.util.List;
import java.util.Map;

public interface PostCafeRepositoryCustom {
    Map<Long, Long> getReviewCountsByMemberIds(List<Long> memberIds);
}
