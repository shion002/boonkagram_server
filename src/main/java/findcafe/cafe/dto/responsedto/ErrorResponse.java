package findcafe.cafe.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String error;
    private String code;
    private long timestamp;

    public ErrorResponse(String error) {
        this.error = error;
        this.code = "ERROR";
        this.timestamp = System.currentTimeMillis();
    }
}
