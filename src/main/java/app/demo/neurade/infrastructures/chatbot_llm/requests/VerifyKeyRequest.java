package app.demo.neurade.infrastructures.chatbot_llm.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VerifyKeyRequest {

    @JsonProperty("api_key")
    private String apiKey;

    private String provider;
}
