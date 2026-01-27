package app.demo.neurade.domain.dtos.externals.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtVerifyKeyResponse {
    @JsonProperty("is_valid")
    private boolean isValid;

    private List<String> models;

    @JsonProperty("error_message")
    private String errorMessage;
}
