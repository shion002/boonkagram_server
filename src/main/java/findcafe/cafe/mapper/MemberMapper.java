package findcafe.cafe.mapper;

import findcafe.cafe.dto.memberdto.MemberRequestDto;
import findcafe.cafe.dto.memberdto.UserResponseDto;
import findcafe.cafe.entity.Member;

public class MemberMapper {

    public static Member toEntity(String username, String encodedPassword, String encodedPhone, String phoneHash, String nickname){
        return new Member(username, encodedPassword, encodedPhone, phoneHash, nickname);
    }

    public static UserResponseDto toUserDto(Member member) {
        return new UserResponseDto(member.getNickname(), member.getProfileImageUrl());
    }
}
