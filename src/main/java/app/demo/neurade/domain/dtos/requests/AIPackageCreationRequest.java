package app.demo.neurade.domain.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIPackageCreationRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Description is required")
    private String description;

    @NotEmpty(message = "API keys must not be empty")
    private List<@NotBlank String> apiKeys;

    @NotNull(message = "Total token is required")
    @Positive(message = "Total token must be positive")
    private Long totalToken;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be >= 0")
    private Double price;

    @NotNull(message = "Token rate limit is required")
    @Positive(message = "Token rate limit must be positive")
    private Long tokenRateLimit;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationInDays;
}
