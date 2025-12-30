package findcafe.cafe.controller;

import findcafe.cafe.dto.postcafedto.PostCafeAndFilteredCafeResponseDto;
import findcafe.cafe.dto.postcafedto.PostCafeRequestDto;
import findcafe.cafe.dto.postcafedto.PostCafeResponseDto;
import findcafe.cafe.dto.postcafedto.PostResponseDto;
import findcafe.cafe.dto.reviewdto.ReviewResponseDto;
import findcafe.cafe.service.PostCafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cafe/")
public class PostCafeController {

    private final PostCafeService postCafeService;

    @GetMapping("{cafeId}/get-post")
    public ResponseEntity<PostCafeResponseDto> getPost(@PathVariable Long cafeId){
        return ResponseEntity.ok(postCafeService.getPost(cafeId));
    }
    @GetMapping("{cafeId}/get-reviews")
    public ResponseEntity<Page<ReviewResponseDto>> getPostReviews(
            @PathVariable Long cafeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "LATEST") String sort
    ) {
        Page<ReviewResponseDto> reviews = postCafeService.getPostReviews(cafeId, page, size, sort);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("get-cafe-post")
    public ResponseEntity<PostCafeAndFilteredCafeResponseDto> getCafePost(@RequestParam Long cafeId) {
        return ResponseEntity.ok(postCafeService.getCafePost(cafeId));
    }

    @PostMapping("/admin/{id}/update")
    public ResponseEntity<PostCafeAndFilteredCafeResponseDto> postingCafe(@PathVariable Long id,
                                         @RequestBody PostCafeRequestDto postCafeRequestDto){
        return ResponseEntity.ok(postCafeService.updatePost(postCafeRequestDto, id));
    }

    @PostMapping("/admin/{id}/delete")
    public ResponseEntity<Void> deleteCafe(@PathVariable Long id){
        postCafeService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/create")
    public ResponseEntity<PostResponseDto> createCafe(@RequestBody PostCafeRequestDto postCafeRequestDto){
        PostResponseDto postCafe = postCafeService.createPost(postCafeRequestDto);
        return ResponseEntity.ok(postCafe);
    }

}
