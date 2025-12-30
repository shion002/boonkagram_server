package findcafe.cafe.controller;

import findcafe.cafe.dto.reviewdto.CanReviewResponse;
import findcafe.cafe.dto.reviewdto.ReviewPresignedResponseDto;
import findcafe.cafe.dto.reviewdto.ReviewRequestDto;
import findcafe.cafe.dto.reviewdto.ReviewResponseDto;
import findcafe.cafe.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/review/")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("create")
    public ResponseEntity<ReviewPresignedResponseDto> createReview(
            @RequestBody ReviewRequestDto reviewRequestDto,
            Authentication authentication
            ) {
        String username = authentication.getName();

        ReviewPresignedResponseDto review = reviewService.createReview(reviewRequestDto, username);
        return ResponseEntity.ok(review);
    }
    @GetMapping("/can-review/{cafeId}")
    public ResponseEntity<CanReviewResponse> canReview(
            @PathVariable Long cafeId,
            Authentication authentication) {

        String username = authentication.getName();
        boolean canReview = reviewService.checkReview(cafeId, username);
        String message = canReview ? null : reviewService.getCannotReviewReason(cafeId, username);

        return ResponseEntity.ok(new CanReviewResponse(canReview, message));
    }
}
