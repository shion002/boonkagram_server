package findcafe.cafe.dto.memberdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponse {

    private Long id;
    private String username;
    private List<String> roles;
    private boolean authenticated;
}
