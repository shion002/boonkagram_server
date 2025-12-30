package findcafe.cafe.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import findcafe.cafe.entity.QReview;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static findcafe.cafe.entity.QReview.*;

@RequiredArgsConstructor
public class PostCafeRepositoryImpl implements PostCafeRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Map<Long, Long> getReviewCountsByMemberIds(List<Long> memberIds) {
        List<Tuple> results = jpaQueryFactory
                .select(review.member.id, review.count())
                .from(review)
                .where(review.member.id.in(memberIds))
                .groupBy(review.member.id)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(review.member.id),
                        tuple -> tuple.get(review.count())
                ));
    }
}
