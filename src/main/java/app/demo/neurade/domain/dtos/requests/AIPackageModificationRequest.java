package app.demo.neurade.domain.dtos.requests;

import app.demo.neurade.domain.models.AIPackage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIPackageModificationRequest {
    private String name;
    private String model;
    private String description;
    private Double price;
    private List<String> apiKeys;
    private Long totalToken;
    private Long tokenRateLimit;
    private Integer durationInDays;
    private AIPackage.Status status;
}
