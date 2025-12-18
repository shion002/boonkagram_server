package findcafe.cafe.controller;

import findcafe.cafe.dto.memberdto.UserDto;
import findcafe.cafe.dto.memberdto.UserResponseDto;
import findcafe.cafe.dto.presigneddto.PresignedUrlRequest;
import findcafe.cafe.dto.presigneddto.PresignedUrlResponse;
import findcafe.cafe.dto.presigneddto.ProfileImageUpdateRequest;
import findcafe.cafe.entity.Member;
import findcafe.cafe.repository.MemberRepository;
import findcafe.cafe.service.MemberService;
import findcafe.cafe.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final MemberService memberService;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;

    @PostMapping("/profile/presigned-url")
    public ResponseEntity<PresignedUrlResponse> getPresignedUrl(
            @RequestBody PresignedUrlRequest request,
            Authentication authentication) {

        try {
            log.info("User: {}", authentication.getName());
            log.info("FileName: {}", request.getFileName());
            log.info("FileType: {}", request.getFileType());

            PresignedUrlResponse response = s3Service.generatePresignedUrl(
                    request.getFileName(),
                    request.getFileType(),
                    "profile/" + authentication.getName()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Presigned Url 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/profile/image")
    public ResponseEntity<Void> updateProfileImage(
            Authentication authentication,
            @RequestBody ProfileImageUpdateRequest request
            ) {
        String username = authentication.getName();
        memberService.updateProfileImage(username, request.getProfileImageUrl());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile/delete")
    public ResponseEntity<Void> deleteProfileImage(Authentication authentication){
        String username = authentication.getName();
        memberService.deleteProfileImage(username);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/profile/profile-check")
    public ResponseEntity<UserResponseDto> profileCheck(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        String username = userDetails.getUsername();
        UserResponseDto userResponse = memberService.getUserResponse(username);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        UserDto userDto = new UserDto(member.getUsername(), member.getGrade().name());
        return ResponseEntity.ok(userDto);
    }
}



















