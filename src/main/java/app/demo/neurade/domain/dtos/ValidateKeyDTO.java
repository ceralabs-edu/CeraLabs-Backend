package app.demo.neurade.domain.dtos;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateKeyDTO {
    private boolean isValid;

    private List<String> models;

    private String errorMessage;
}
