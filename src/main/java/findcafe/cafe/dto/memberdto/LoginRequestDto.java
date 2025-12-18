package findcafe.cafe.dto.memberdto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "아이디 입력은 필수입니다")
    private String username;
    @NotBlank(message = "비밀번호 입력은 필수입니다")
    private String password;
}
