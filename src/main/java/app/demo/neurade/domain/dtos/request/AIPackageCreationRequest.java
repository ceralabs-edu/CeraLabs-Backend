package app.demo.neurade.domain.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIPackageCreationRequest {
    private String name;
    private String model;
    private Long totalToken;
    private Double price;
    private String tokenRateLimit;
    private Integer durationInDays;
}
