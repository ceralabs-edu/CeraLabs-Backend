package app.demo.neurade.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class AIPackageDTO {
    private Integer id;
    private String name;
    private String model;
    private String description;
    private Long totalToken;
    private Double price;
    private Long tokenRateLimit;
    private Integer durationInDays;
}
