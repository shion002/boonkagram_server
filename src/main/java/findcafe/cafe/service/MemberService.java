package findcafe.cafe.service;

import findcafe.cafe.dto.memberdto.MemberRequestDto;
import findcafe.cafe.dto.memberdto.UserResponseDto;
import findcafe.cafe.entity.Member;
import findcafe.cafe.mapper.MemberMapper;
import findcafe.cafe.repository.MemberRepository;
import findcafe.cafe.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;
    private final EncryptionUtil encryptionUtil;
    private final S3Service s3Service;

    @Transactional
    public void createMember(MemberRequestDto memberRequestDto){

        String plainPhone = memberRequestDto.getPhone();

        if(memberRepository.findByUsername(memberRequestDto.getUsername()).isPresent()){
            throw new RuntimeException("이미 존재하는 계정입니다.");
        }

        if(!smsService.isPhoneVerified(memberRequestDto.getPhone())){
            throw new RuntimeException("전화번호 인증이 완료되지 않았거나 만료되었습니다");
        }

        String phoneHash = DigestUtils.sha256Hex(plainPhone);

        if(memberRepository.existsByPhoneHash(phoneHash)){
            throw new RuntimeException("이미 가입된 전화번호입니다");
        }

        if(memberRepository.findByNickname(memberRequestDto.getNickname()).isPresent()){
            throw new RuntimeException("이미 등록된 닉네임입니다");
        }

        String passwordEncode = passwordEncoder.encode(memberRequestDto.getPassword());
        String encryptedPhone = encryptionUtil.encrypt(plainPhone);

        Member member = MemberMapper.toEntity(memberRequestDto.getUsername(),
                passwordEncode, encryptedPhone, phoneHash, memberRequestDto.getNickname());

        memberRepository.save(member);

        smsService.clearVerifiedStatus(memberRequestDto.getPhone());

        log.info("회원가입 완료");
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다"));
    }

    public boolean isUsernameAvailable(String username){
        return memberRepository.findByUsername(username).isEmpty();
    }

    public boolean isPhoneAvailable(String phone) {
        String phoneHash = DigestUtils.sha256Hex(phone);
        return !memberRepository.existsByPhoneHash(phoneHash);
    }

    public boolean isNicknameAvailable(String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();
    }

    @Transactional
    public void updateProfileImage(String username, String profileImageUrl) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다"));

        if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().isEmpty()) {
            s3Service.deleteFile(member.getProfileImageUrl());
        }

        member.updateProfileImage(profileImageUrl);
    }

    @Transactional
    public void deleteProfileImage(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다"));

        if (member.getProfileImageUrl() == null || member.getProfileImageUrl().isEmpty()) {
            log.info("삭제할 프로필 이미지가 없습니다.");
            return;
        }

        s3Service.deleteFile(member.getProfileImageUrl());
        member.deleteProfileImage();
    }


    public UserResponseDto getUserResponse(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다"));

        return MemberMapper.toUserDto(member);
    }
}


















