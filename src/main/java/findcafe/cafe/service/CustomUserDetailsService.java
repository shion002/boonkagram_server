package findcafe.cafe.service;

import findcafe.cafe.entity.Member;
import findcafe.cafe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("아이디 또는 비밀번호를 확인하세요"));
        return new User(member.getUsername(), member.getPassword(),
                true, true, true, true, getAuthorities(member));
    }

    private List<GrantedAuthority> getAuthorities(Member member){

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        switch (member.getGrade()) {
            case ADMIN:
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
            case VIP:
                authorities.add(new SimpleGrantedAuthority("ROLE_VIP"));
                break;
            case BASIC:
            default:
                break;
        }

        return authorities;
    }
}
