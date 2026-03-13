package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.AIPackageInstance;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIPackageInstanceDTO {
    private UUID instanceId;
    private Integer aiPackageId;
    private Long classId;
    private Long purchaserId;
    private Long remainingToken;
    private Long totalToken;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
    private AIPackageInstance.Status status;
}
