package findcafe.cafe.service;

import findcafe.cafe.dto.filteredcafedto.FilteredCafeResponseDto;
import findcafe.cafe.dto.postcafedto.PostCafeAndFilteredCafeResponseDto;
import findcafe.cafe.dto.postcafedto.PostCafeRequestDto;
import findcafe.cafe.dto.postcafedto.PostCafeResponseDto;
import findcafe.cafe.dto.postcafedto.PostResponseDto;
import findcafe.cafe.dto.presigneddto.PresignedUrlResponse;
import findcafe.cafe.dto.reviewdto.ReviewResponseDto;
import findcafe.cafe.dto.utildto.ImageDto;
import findcafe.cafe.entity.FilteredCafe;
import findcafe.cafe.entity.PostCafe;
import findcafe.cafe.entity.Review;
import findcafe.cafe.mapper.FilteredCafeMapper;
import findcafe.cafe.mapper.PostCafeMapper;
import findcafe.cafe.mapper.ReviewMapper;
import findcafe.cafe.repository.FilteredCafeRepository;
import findcafe.cafe.repository.PostCafeRepository;
import findcafe.cafe.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCafeService {

    private final PostCafeRepository postCafeRepository;
    private final FilteredCafeRepository filteredCafeRepository;
    private final S3Service s3Service;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;

    @Transactional(readOnly = true)
    public PostCafeResponseDto getPost(Long cafeId){

        PostCafe postCafe = postCafeRepository
                .findByFilteredCafeId(cafeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 포스트 입니다"));

        return PostCafeMapper.toDto(postCafe);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getPostReviews(Long cafeId, int page, int size, String sort){
        Pageable pageable = createPageable(page, size, sort);

        Page<Review> reviews = reviewRepository.findByPostCafeIdWithMember(cafeId, pageable);

        List<Long> memberIds = reviews.getContent().stream()
                .map(review -> review.getMember().getId()).distinct().toList();

        Map<Long, Long> reviewCountMap = postCafeRepository.getReviewCountsByMemberIds(memberIds);

        return reviews.map(review -> ReviewMapper.toDto(review, reviewCountMap));
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort sortOrder = switch (sort) {
            case "RATING_HIGH" -> Sort.by(Sort.Direction.DESC, "ratingScore");
            case "RATING_LOW" -> Sort.by(Sort.Direction.ASC, "ratingScore");
            default -> Sort.by(Sort.Direction.DESC, "createDate");
        };
        return PageRequest.of(page, size, sortOrder);
    }

    public PostCafeAndFilteredCafeResponseDto getCafePost(Long cafeId) {
        PostCafe postCafe = postCafeRepository
                .findByFilteredCafeId(cafeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 포스트 입니다"));
        FilteredCafe filteredCafe = filteredCafeRepository
                .findById(cafeId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 카페 입니다"));

        PostCafeResponseDto postCafeResponseDto = PostCafeMapper.toDto(postCafe);
        FilteredCafeResponseDto filteredCafeResponseDto = FilteredCafeMapper.toDto(filteredCafe);

        return new PostCafeAndFilteredCafeResponseDto(postCafeResponseDto, filteredCafeResponseDto);
    }

    @Transactional
    public void deletePost(Long id){
        FilteredCafe filteredCafe = filteredCafeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 카페입니다"));

        String filteredCafeName = filteredCafe.getName();

        PostCafe postCafe = postCafeRepository.findByFilteredCafeId(filteredCafe.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 포스트입니다"));

        List<Review> reviews = postCafe.getReviews();



        if(filteredCafe.getS3FolderPath() != null) {
            s3Service.deleteFolder("cafe/" + filteredCafe.getS3FolderPath());
        }

        if(reviews != null) {
            for (Review review : reviews) {
                reviewService.deleteReview(review.getId());
            }
        }

        postCafeRepository.delete(postCafe);
        filteredCafeRepository.delete(filteredCafe);

        log.info("{} 삭제 완료", filteredCafeName);
    }

    @Transactional
    public PostResponseDto createPost(PostCafeRequestDto postCafeRequestDto){

        String s3FolderPath = generateS3FolderPath(postCafeRequestDto.getCafeName());

        PresignedUrlResponse thumbnailPresignedUrl = null;
        List<PresignedUrlResponse> imagePresignedUrls = new ArrayList<>();

        if(postCafeRequestDto.getThumbnail() != null) {
            thumbnailPresignedUrl = s3Service.generatePresignedUrl(
                    postCafeRequestDto.getThumbnail().getFileName(),
                    postCafeRequestDto.getThumbnail().getFileType(),
                    s3FolderPath + "/thumbnail"
            );
        }

        if(postCafeRequestDto.getImageUrls() != null && !postCafeRequestDto.getImageUrls().isEmpty()) {
            for (ImageDto imageDto : postCafeRequestDto.getImageUrls()) {
                PresignedUrlResponse imagePresignedUrl = s3Service.generatePresignedUrl(
                        imageDto.getFileName(),
                        imageDto.getFileType(),
                        s3FolderPath + "/images"
                );
                imagePresignedUrls.add(imagePresignedUrl);
            }
        }

        FilteredCafe filteredCafe = new FilteredCafe(
                postCafeRequestDto,
                thumbnailPresignedUrl != null ? thumbnailPresignedUrl.getFileUrl() : null,
                s3FolderPath
        );
        filteredCafeRepository.save(filteredCafe);

        PostCafe postCafe = new PostCafe();
        postCafe.setCreateData(postCafeRequestDto, filteredCafe.getId(),
                imagePresignedUrls.stream().map(PresignedUrlResponse::getFileUrl).collect(Collectors.toList()));
        postCafeRepository.save(postCafe);

        log.info("{} 저장완료", filteredCafe.getName());

        return new PostResponseDto(filteredCafe.getId(), thumbnailPresignedUrl, imagePresignedUrls);
    }

    private String generateS3FolderPath(String cafeName) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String sanitizedName = cafeName.replaceAll("[^a-zA-Z0-9가-힣\\s]", "").replaceAll("\\s+", "_");

        return uuid + "_" + sanitizedName;
    }

    @Transactional
    public PostCafeAndFilteredCafeResponseDto updatePost(PostCafeRequestDto postCafeRequestDto, Long id){
        FilteredCafe filteredCafe = filteredCafeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 카페입니다"));

        PostCafe postCafe = postCafeRepository.findByFilteredCafeId(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Post입니다"));

        FilteredCafeResponseDto filteredCafeResponseDto = FilteredCafeMapper.toDto(filteredCafe);
        PostCafeResponseDto postCafeResponseDto = PostCafeMapper.toDto(postCafe);

        String s3FolderPath = filteredCafe.getS3FolderPath();

        if(s3FolderPath == null || s3FolderPath.isEmpty()) {
            s3FolderPath = generateS3FolderPath(postCafeRequestDto.getCafeName());
            filteredCafe.updateS3FolderPath(s3FolderPath);
        }

        PresignedUrlResponse thumbnailPresignedUrl = null;
        String finalThumbnail = filteredCafeResponseDto.getThumbnail();
        List<PresignedUrlResponse> imagePresignedUrls = new ArrayList<>();

        if(postCafeRequestDto.getThumbnail() != null) {
            if (filteredCafeResponseDto.getThumbnail() != null) {
                s3Service.deleteFile(filteredCafeResponseDto.getThumbnail());
            }
            thumbnailPresignedUrl = s3Service.generatePresignedUrl(
                    postCafeRequestDto.getThumbnail().getFileName(),
                    postCafeRequestDto.getThumbnail().getFileType(),
                    s3FolderPath + "/thumbnail"
            );
            finalThumbnail = thumbnailPresignedUrl.getFileUrl();
        } else if(Boolean.TRUE.equals(postCafeRequestDto.getDeleteThumbnail())){
            if (filteredCafeResponseDto.getThumbnail() != null) {
                s3Service.deleteFile(filteredCafeResponseDto.getThumbnail());
            }
            finalThumbnail = null;
        }

        if(postCafeRequestDto.getImageUrls() != null && !postCafeRequestDto.getImageUrls().isEmpty()) {
            for (ImageDto imageDto : postCafeRequestDto.getImageUrls()) {
                PresignedUrlResponse imagePresignedUrl = s3Service.generatePresignedUrl(
                        imageDto.getFileName(),
                        imageDto.getFileType(),
                        s3FolderPath + "/images"
                );
                imagePresignedUrls.add(imagePresignedUrl);
            }
        }

        List<String> finalImageUrls = new ArrayList<>();

        if(postCafeRequestDto.getExistingImageUrls() != null) {
            finalImageUrls.addAll(postCafeRequestDto.getExistingImageUrls());

            List<String> currentImages = postCafeResponseDto.getImageUrls();
            if(currentImages != null) {
                for (String currentImage : currentImages) {
                    if (!postCafeRequestDto.getExistingImageUrls().contains(currentImage)) {
                        s3Service.deleteFile(currentImage);
                    }
                }
            }
        } else {
            if(postCafeResponseDto.getImageUrls() != null) {
                for (String imageUrl : postCafeResponseDto.getImageUrls()) {
                    s3Service.deleteFile(imageUrl);
                }
            }
        }

        finalImageUrls.addAll(imagePresignedUrls.stream().map(PresignedUrlResponse::getFileUrl).toList());

        FilteredCafeResponseDto filteredCafeUpdateDto = filteredCafe
                .update(postCafeRequestDto, finalThumbnail);
        PostCafeResponseDto postCafeUpdateDto = postCafe.setUpdateData(postCafeRequestDto, finalImageUrls);

        log.info("{} 업데이트 완료", filteredCafe.getName());

        return new PostCafeAndFilteredCafeResponseDto(postCafeUpdateDto, filteredCafeUpdateDto, thumbnailPresignedUrl, imagePresignedUrls);
    }

    @Transactional
    public void updatePostBasic(){
        List<FilteredCafeResponseDto> cafeResponseDtos = filteredCafeRepository
                .findAll().stream().map(FilteredCafeMapper::toDto).toList();

        for (FilteredCafeResponseDto cafeResponseDto : cafeResponseDtos) {
            PostCafeRequestDto postCafeRequestDto = new PostCafeRequestDto(cafeResponseDto.getName(),
                    cafeResponseDto.getAddress(), cafeResponseDto.getId());
            PostCafe postCafe = PostCafeMapper.toEntity(postCafeRequestDto);
            postCafeRepository.save(postCafe);
            log.info("저장완료: {}", cafeResponseDto.getName());
        }
    }
}
