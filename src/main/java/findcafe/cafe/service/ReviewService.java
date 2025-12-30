package findcafe.cafe.service;

import findcafe.cafe.dto.presigneddto.PresignedUrlResponse;
import findcafe.cafe.dto.reviewdto.ReviewPresignedResponseDto;
import findcafe.cafe.dto.reviewdto.ReviewRequestDto;
import findcafe.cafe.dto.reviewdto.ReviewResponseDto;
import findcafe.cafe.dto.reviewdto.ReviewStatsDto;
import findcafe.cafe.dto.utildto.ImageDto;
import findcafe.cafe.entity.FilteredCafe;
import findcafe.cafe.entity.Member;
import findcafe.cafe.entity.PostCafe;
import findcafe.cafe.entity.Review;
import findcafe.cafe.mapper.ReviewMapper;
import findcafe.cafe.repository.FilteredCafeRepository;
import findcafe.cafe.repository.MemberRepository;
import findcafe.cafe.repository.PostCafeRepository;
import findcafe.cafe.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PostCafeRepository postCafeRepository;
    private final S3Service s3Service;
    private final FilterCafeService filterCafeService;
    private final FilteredCafeRepository filteredCafeRepository;

    @Transactional(readOnly = true)
    public boolean checkReview(Long filteredCafeId, String username){
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다"));
        PostCafe postCafe = postCafeRepository.findByFilteredCafeId(filteredCafeId).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다"));

        if (reviewRepository.existsByMemberAndPostCafe(member, postCafe)) {
            return false;
        }

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        long todayReviewCount = reviewRepository.countTodayReviews(member, startOfDay);

        return todayReviewCount < 2;
    }
    @Transactional(readOnly = true)
    public String getCannotReviewReason(Long filteredCafeId, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다"));

        PostCafe postCafe = postCafeRepository.findByFilteredCafeId(filteredCafeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 포스트입니다"));

        if (reviewRepository.existsByMemberAndPostCafe(member, postCafe)) {
            return "이미 이 카페에 리뷰를 작성하셨습니다.";
        }

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        long todayReviewCount = reviewRepository.countTodayReviews(member, startOfDay);

        if (todayReviewCount >= 2) {
            return "하루에 최대 2개의 리뷰만 작성할 수 있습니다.";
        }

        return null;
    }

    @Transactional
    public ReviewPresignedResponseDto createReview(ReviewRequestDto reviewRequestDto, String username) {

        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다"));
        PostCafe postCafe = postCafeRepository.findById(reviewRequestDto.getPostId()).orElseThrow(() -> new RuntimeException("존재하지 않는 포스트입니다"));

        String s3FolderPath = generateS3FolderPath(username);

        List<PresignedUrlResponse> reviewImages = new ArrayList<>();

        if(reviewRequestDto.getReviewImages() != null && !reviewRequestDto.getReviewImages().isEmpty()) {
            for (ImageDto reviewImage : reviewRequestDto.getReviewImages()) {
                PresignedUrlResponse imageUrlResponse = s3Service.generatePresignedUrl(
                        reviewImage.getFileName(),
                        reviewImage.getFileType(),
                        "reviews/" + reviewRequestDto.getPostId() + "/" + s3FolderPath
                );
                reviewImages.add(imageUrlResponse);
            }
        }
        Review review = ReviewMapper.toEntity(reviewRequestDto, member, postCafe,
                reviewImages.stream().map(PresignedUrlResponse::getFileUrl).collect(Collectors.toList()), s3FolderPath);

        reviewRepository.save(review);

        FilteredCafe filteredCafe = filteredCafeRepository.findById(postCafe.getFilteredCafeId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카페입니다"));

        ReviewStatsDto stats = reviewRepository.getReviewStatsByFilteredCafeId(reviewRequestDto.getPostId());

        log.info("통계 조회 - 평점: {}, 리뷰수: {}", stats.getAverageRating(), stats.getReviewCountAsInt());

        filteredCafe.updateReviewStats(stats.getAverageRating(), stats.getReviewCountAsInt());

        filteredCafeRepository.saveAndFlush(filteredCafe);

        log.info("FilteredCafe 업데이트 완료 - ID: {}, 평점: {}, 리뷰수: {}",
                filteredCafe.getId(), filteredCafe.getRating(), filteredCafe.getReviewCount());

        return new ReviewPresignedResponseDto(reviewImages);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 리뷰입니다"));

        Long filteredCafeId = review.getPostCafe().getFilteredCafeId();

        if(review.getS3FolderPath() != null) {
            s3Service.deleteFolder("cafe/reviews/" + review.getPostCafe().getId() + "/" + review.getS3FolderPath());
        }

        reviewRepository.delete(review);

        filterCafeService.updateFilteredCafeReviewStats(filteredCafeId);
    }

    private String generateS3FolderPath(String username) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String sanitizedName = username.replaceAll("[^a-zA-Z0-9가-힣\\s]", "").replaceAll("\\s+", "_");

        return uuid + "_" + sanitizedName;
    }
}
