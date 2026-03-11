package app.demo.neurade.domain.dtos.requests;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ModifyAIPackageInstanceRequest {
    private LocalDateTime expiryDate;
    private Integer remainingToken;
}

