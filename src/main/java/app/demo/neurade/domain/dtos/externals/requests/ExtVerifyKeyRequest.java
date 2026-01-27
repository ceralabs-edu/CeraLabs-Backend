package app.demo.neurade.domain.dtos.externals.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ExtVerifyKeyRequest {

    @JsonProperty("api_key")
    private String apiKey;

    private String provider;
}
