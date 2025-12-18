package findcafe.cafe.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.searchdto.CafeNameAddressDto;
import findcafe.cafe.entity.FilteredCafe;
import findcafe.cafe.entity.QFilteredCafe;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static findcafe.cafe.entity.QFilteredCafe.*;

@RequiredArgsConstructor
public class FilteredCafeRepositoryImpl implements FilteredCafeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public void updateCreateData(LocalDateTime dateTime) {
        queryFactory
                .update(filteredCafe)
                .set(filteredCafe.createData, dateTime)
                .execute();
    }

    @Override
    public List<CafeNameAddressDto> findTop10NameWithAddressByNameContaining(String search) {
        String[] keywords = search.trim().split("\\s+");

        BooleanBuilder builder = new BooleanBuilder();
        for (String keyword : keywords) {
            builder.and(
                    filteredCafe.name.like("%" + keyword + "%")
                            .or(filteredCafe.address.like("%" + keyword + "%"))
            );
        }

        return queryFactory
                .select(Projections.constructor(
                        CafeNameAddressDto.class,
                        filteredCafe.id,
                        filteredCafe.name,
                        filteredCafe.address
                ))
                .from(filteredCafe)
                .where(builder)
                .orderBy(
                        new CaseBuilder()
                                .when(filteredCafe.name.startsWith(search)).then(1)
                                .otherwise(2)
                                .asc(),
                        new CaseBuilder()
                                .when(filteredCafe.name.like("%" + search + "%")).then(1)
                                .otherwise(2)
                                .asc(),
                        filteredCafe.name.asc()
                )
                .limit(10)
                .fetch();
    }

    @Override
    public Page<FilteredCafeResponseDto> searchDescendFilterCafe(int page, int size, String search) {
        String[] keywords = search.trim().split("\\s+");

        BooleanBuilder builder = new BooleanBuilder();
        for (String keyword : keywords) {
            builder.and(
                    filteredCafe.name.like("%" + keyword + "%")
                            .or(filteredCafe.address.like("%" + keyword + "%"))
            );
        }

        OrderSpecifier<?>[] orderSpecifiers = {
                new CaseBuilder()
                        .when(filteredCafe.name.startsWith(search)).then(1)
                        .otherwise(2)
                        .asc(),
                new CaseBuilder()
                        .when(filteredCafe.name.like("%" + search + "%")).then(1)
                        .otherwise(2)
                        .asc(),
                filteredCafe.name.asc()
        };

        List<FilteredCafeResponseDto> content = queryFactory
                .select(Projections.constructor(
                        FilteredCafeResponseDto.class,
                        filteredCafe.id,
                        filteredCafe.name,
                        filteredCafe.address,
                        filteredCafe.lat,
                        filteredCafe.lon,
                        filteredCafe.thumbnail,
                        filteredCafe.intro
                ))
                .from(filteredCafe)
                .where(builder)
                .orderBy(orderSpecifiers)
                .offset((long) page * size)
                .limit(size)
                .fetch();

        Long total = queryFactory
                .select(filteredCafe.count())
                .from(filteredCafe)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, PageRequest.of(page, size), total != null ? total : 0L);
    }

    @Override
    public Page<FilteredCafeResponseDto> findNearbyCafes(Double lat, Double lon, int page, int size) {
        NumberExpression<Double> distance = calculateDistance(lat, lon);

        BooleanExpression whereCondition = filteredCafe.lat.isNotNull()
                .and(filteredCafe.lon.isNotNull())
                .and(distance.loe(10.0));

        List<FilteredCafeResponseDto> content = queryFactory
                .select(Projections.constructor(
                        FilteredCafeResponseDto.class,
                        filteredCafe.id,
                        filteredCafe.name,
                        filteredCafe.address,
                        filteredCafe.lat,
                        filteredCafe.lon,
                        filteredCafe.thumbnail,
                        filteredCafe.intro,
                        distance.as("distance")
                ))
                .from(filteredCafe)
                .where(whereCondition)
                .orderBy(distance.asc()) // 거리순 정렬
                .offset((long) page * size)
                .limit(size)
                .fetch();

        Long total = queryFactory
                .select(filteredCafe.count())
                .from(filteredCafe)
                .where(whereCondition)
                .fetchOne();

        return new PageImpl<>(content, PageRequest.of(page, size), total != null ? total : 0L);
    }

    private NumberExpression<Double> calculateDistance(Double userLat, Double userLon) {
        // 지구 반지름 (km)
        double earthRadius = 6371.0;

        // 위도를 라디안으로 변환
        NumberExpression<Double> lat1Rad = Expressions.numberTemplate(Double.class,
                "RADIANS({0})", userLat);
        NumberExpression<Double> lat2Rad = Expressions.numberTemplate(Double.class,
                "RADIANS({0})", filteredCafe.lat);

        // 위도 차이
        NumberExpression<Double> latDiff = Expressions.numberTemplate(Double.class,
                "RADIANS({0} - {1})", filteredCafe.lat, userLat);

        // 경도 차이
        NumberExpression<Double> lonDiff = Expressions.numberTemplate(Double.class,
                "RADIANS({0} - {1})", filteredCafe.lon, userLon);

        // Haversine 공식
        // a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)
        NumberExpression<Double> a = Expressions.numberTemplate(Double.class,
                "POWER(SIN({0} / 2), 2) + COS({1}) * COS({2}) * POWER(SIN({3} / 2), 2)",
                latDiff, lat1Rad, lat2Rad, lonDiff);

        // c = 2 * atan2(√a, √(1-a))
        NumberExpression<Double> c = Expressions.numberTemplate(Double.class,
                "2 * ATAN2(SQRT({0}), SQRT(1 - {0}))",
                a);

        // distance = R * c
        return Expressions.numberTemplate(Double.class,
                "{0} * {1}",
                earthRadius, c);
    }

}
