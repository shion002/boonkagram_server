package findcafe.cafe.service;

import findcafe.cafe.dto.reviewdto.ReviewRequestDto;
import findcafe.cafe.dto.reviewdto.ReviewResponseDto;
import findcafe.cafe.entity.Member;
import findcafe.cafe.entity.PostCafe;
import findcafe.cafe.entity.ReviewScore;
import findcafe.cafe.repository.MemberRepository;
import findcafe.cafe.repository.PostCafeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostCafeService postCafeService;

    @Autowired
    private PostCafeRepository postCafeRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void createReview() {
        Member member = memberRepository.findByUsername("testUser").orElseThrow(() -> new UsernameNotFoundException("멤버오류"));
        PostCafe postCafe = postCafeRepository.findById(47203L).orElseThrow(() -> new RuntimeException("포스트 오류"));

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto("분위기좋아요",
                null, new ReviewScore(5, 5, 5, 5), 47203L);
    }

}















