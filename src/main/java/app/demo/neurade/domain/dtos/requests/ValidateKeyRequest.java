package app.demo.neurade.domain.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidateKeyRequest {

    @NotBlank(message = "apiKey must not be blank")
    private String apiKey;

    @NotBlank(message = "provider must not be blank")
    private String provider;
}
