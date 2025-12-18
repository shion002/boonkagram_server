package findcafe.cafe.dto.memberdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private String username;
    private Object authorities;
    private LocalDateTime localDateTime;
    private String message;
    private boolean success;
}
