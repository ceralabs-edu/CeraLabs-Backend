package app.demo.neurade.domain.dtos.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenUsageLimit {
    private Long tokenLimit;
    private Long rateLimitTokenMax;
    private Integer durationInDays;
}
