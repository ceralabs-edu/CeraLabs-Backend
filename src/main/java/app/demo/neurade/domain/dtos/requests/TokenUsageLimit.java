package app.demo.neurade.domain.dtos.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUsageLimit {
    private Long tokenLimit;
    private Long rateLimitTokenMax;
    private Integer durationInDays;
}
