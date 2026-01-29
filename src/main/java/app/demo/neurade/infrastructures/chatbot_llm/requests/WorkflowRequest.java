package app.demo.neurade.infrastructures.chatbot_llm.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRequest {

    @JsonProperty("api_key")
    private String apiKey;

    private String model;

    private List<Query> queries;

    @JsonProperty("image_urls")
    private List<String> files;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Query {
        private String role;
        private String content;
    }
}
