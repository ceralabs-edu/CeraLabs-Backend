package app.demo.neurade.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class AIPackageInstanceDTO {
    private UUID instanceId;
    private Integer aiPackageId;
    private Long classId;
    private Long purchaserId;
    private Long remainingToken;
    private Long totalToken;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
}
