package findcafe.cafe.repository;

import findcafe.cafe.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByNickname(String nickname);

    boolean existsByPhoneHash(String phoneHash);

    void deleteByProfileImageUrl(String profileImageUrl);
}
