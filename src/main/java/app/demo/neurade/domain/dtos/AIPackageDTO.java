package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.AIPackage;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIPackageDTO {
    private Integer id;
    private String name;
    private String model;
    private String description;
    private Long totalToken;
    private Double price;
    private Long tokenRateLimit;
    private Integer durationInDays;
    private AIPackage.Status status;
}
