package findcafe.cafe.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import findcafe.cafe.service.FilterCafeService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;


@DataJpaTest
@Import({FilteredCafeRepositoryImpl.class,
        FilterCafeService.class
})
class FilterCafeRepositoryImplTest {

    @Autowired
    private FilterCafeService cafeService;

    @Test
    void filterCafeDataInput(){
        cafeService.updateAllData();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

}