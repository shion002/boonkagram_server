package findcafe.cafe.dto.memberdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {

    @NotBlank(message = "아이디는 필수 입력입니다")
    @Size(min = 6, message = "아이디는 최소 6자 이상이여야 합니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력입니다")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "비밀번호는 8자 이상이며, 영문, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "전화번호는 필수 입력입니다")
    @Size(min = 9, message = "전화번호는 최소 9자 이상이여야 합니다")
    private String phone;

    @NotBlank(message = "닉네임은 필수 입력입니다")
    @Size(min = 2, message = "닉네임은 최소 2자 이상이여야 합니다")
    private String nickname;
}
