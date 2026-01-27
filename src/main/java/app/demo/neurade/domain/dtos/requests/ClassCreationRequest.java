package app.demo.neurade.domain.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a class")
public class ClassCreationRequest {

    @Schema(
            description = "Class name",
            example = "Math 10A",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    private String className;

    @Schema(
            description = "Class description",
            example = "This is a math class for 10th grade students.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String description;
}
