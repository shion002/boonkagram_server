package findcafe.cafe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
public class Member implements UserDetails {

    @Id @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String phone;
    private String phoneHash;
    private String nickname;
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private MemberGrade grade;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    public Member() {
    }

    public Member(String username, String password, String phone, String phoneHash, String nickname) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.phoneHash = phoneHash;
        this.nickname = nickname;
        this.grade = MemberGrade.BASIC;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    public void deleteProfileImage() {
        this.profileImageUrl = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (grade == MemberGrade.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
