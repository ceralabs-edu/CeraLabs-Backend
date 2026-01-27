package app.demo.neurade.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ValidateKeyDTO {
    private boolean isValid;

    private List<String> models;

    private String errorMessage;
}
