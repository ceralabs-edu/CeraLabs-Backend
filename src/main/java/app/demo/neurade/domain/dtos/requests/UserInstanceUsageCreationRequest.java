package app.demo.neurade.domain.dtos.requests;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInstanceUsageCreationRequest {
    private Long userId;
    private UUID instanceId;
    private TokenUsageLimit tokenLimit;
}
