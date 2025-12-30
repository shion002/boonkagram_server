package findcafe.cafe.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import findcafe.cafe.dto.reviewdto.QReviewStatsDto;
import findcafe.cafe.dto.reviewdto.ReviewStatsDto;
import findcafe.cafe.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static findcafe.cafe.entity.QMember.*;
import static findcafe.cafe.entity.QPostCafe.*;
import static findcafe.cafe.entity.QReview.*;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<Review> findByPostCafeIdWithMember(Long cafeId, Pageable pageable) {
        List<Review> reviews = jpaQueryFactory
                .selectFrom(review)
                .join(review.member, member).fetchJoin()
                .where(review.postCafe.filteredCafeId.eq(cafeId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        Long total = jpaQueryFactory
                .select(review.count())
                .from(review)
                .where(review.postCafe.filteredCafeId.eq(cafeId))
                .fetchOne();

        return new PageImpl<>(reviews, pageable, total != null ? total : 0L);
    }

    @Override
    public long countTodayReviews(Member member, LocalDateTime startOfDay) {
        Long count = jpaQueryFactory
                .select(review.count())
                .from(review)
                .where(review.member.eq(member).and(review.createDate.goe(startOfDay)))
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public boolean existsByMemberAndPostCafe(Member member, PostCafe postCafe) {
        Integer exists = jpaQueryFactory
                .selectOne()
                .from(review)
                .where(
                        review.member.eq(member)
                                .and(review.postCafe.eq(postCafe))
                )
                .fetchFirst();
        return exists != null;
    }

    @Override
    public ReviewStatsDto getReviewStatsByFilteredCafeId(Long filteredCafeId) {
        ReviewStatsDto result = jpaQueryFactory
                .select(new QReviewStatsDto(
                        review.ratingScore.avg().coalesce(0.0),
                        review.count().coalesce(0L)
                ))
                .from(review)
                .where(review.postCafe.id.eq(filteredCafeId))
                .fetchOne();
        return result != null ? result : new ReviewStatsDto(0.0, 0L);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        return sort.stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    String property = order.getProperty();

                    return switch (property) {
                        case "ratingScore" -> new OrderSpecifier<>(direction, review.ratingScore);
                        default -> new OrderSpecifier<>(direction, review.createDate);
                    };
                })
                .toArray(OrderSpecifier[]::new);
    }
}
