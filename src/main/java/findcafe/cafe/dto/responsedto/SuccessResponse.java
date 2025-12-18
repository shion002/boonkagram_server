package findcafe.cafe.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse {

    private String message;
    private boolean success;
    private long timestamp;

    public SuccessResponse(String message) {
        this.message = message;
        this.success = true;
        this.timestamp = System.currentTimeMillis();
    }
}
