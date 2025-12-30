package findcafe.cafe.controller;

import findcafe.cafe.dto.memberdto.LoginRequestDto;
import findcafe.cafe.dto.memberdto.LoginResponseDto;
import findcafe.cafe.dto.memberdto.MemberInfoResponse;
import findcafe.cafe.dto.memberdto.MemberRequestDto;
import findcafe.cafe.dto.responsedto.ErrorResponse;
import findcafe.cafe.dto.responsedto.SuccessResponse;
import findcafe.cafe.entity.Member;
import findcafe.cafe.service.MemberService;
import findcafe.cafe.service.SmsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SmsService smsService;
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto loginRequest, HttpSession session){
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authenticate);
            SecurityContextHolder.setContext(securityContext);

            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);

            LoginResponseDto response = new LoginResponseDto(
                    authenticate.getName(),
                    authenticate.getAuthorities(),
                    LocalDateTime.now(),
                    "로그인 성공",
                    true
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("로그인 실패: 아이디 또는 비밀번호를 확인하세요."));
        }

    }

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestParam String phoneNumber) {
        try {
            smsService.sendVerificationCode(phoneNumber);
            return ResponseEntity.ok("인증번호가 발송되었습니다");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Boolean> verifyCode(
            @RequestParam String phoneNumber,
            @RequestParam String code
    ) {
        boolean isValid = smsService.verifyCode(phoneNumber, code);

        if(isValid) {
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.badRequest().body(false);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid MemberRequestDto memberRequestDto){
        try {
            memberService.createMember(memberRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("회원가입이 완료되었습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("회원가입 처리 중 오류가 발생했습니다"));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean available = memberService.isUsernameAvailable(username);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhone(@RequestParam String phone) {
        boolean available = memberService.isPhoneAvailable(phone);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean available = memberService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(available);
    }

}










