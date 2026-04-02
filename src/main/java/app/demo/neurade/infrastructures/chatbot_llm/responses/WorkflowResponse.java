package app.demo.neurade.infrastructures.chatbot_llm.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponse {

    private static final ObjectMapper mapper = new ObjectMapper();

    private Guardian guardian;

    @JsonProperty("assistant")
    private Assistant assistant;

    @Getter
    public static class Guardian {
        @Getter
        public static class GuardianResponse {
            @JsonProperty("problem_type")
            private List<String> problemType;
            private String difficulty;
            private String route;

            @Override
            public String toString() {
                try {
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
                } catch (Exception e) {
                    return "{\"error\":\"Could not serialize Assistant to JSON\"}";
                }
            }
        }

        @JsonProperty("response")
        private GuardianResponse response;

        @JsonProperty("response_raw")
        @JsonIgnore
        private List<List<Object>> responseRaw;

        public UsageMetadata usageMetadata() {
            return extractUsageMetadata(responseRaw);
        }
    }

    @Getter
    public static class Assistant {
        @Getter
        public static class AssistantResponse {
            private String approach;
            private List<SolutionStep> solution;

            @JsonProperty("final_answer")
            private String finalAnswer;

            @Override
            public String toString() {
                try {
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
                } catch (Exception e) {
                    return "{\"error\":\"Could not serialize Assistant to JSON\"}";
                }
            }
        }

        @JsonProperty("response")
        private AssistantResponse response;

        @JsonProperty("response_raw")
        @JsonIgnore
        private List<List<Object>> responseRaw;

        public UsageMetadata usageMetadata() {
            return extractUsageMetadata(responseRaw);
        }
    }

    @Getter
    public static class SolutionStep {
        private String title;
        private String solving;
        private String explanation;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageMetadata {
        @JsonProperty("input_tokens")
        private long inputTokens;

        @JsonProperty("output_tokens")
        private long outputTokens;

        @JsonProperty("total_tokens")
        private long totalTokens;
    }

    private static UsageMetadata extractUsageMetadata(List<List<Object>> responseRaw) {
        if (responseRaw == null) return null;

        return responseRaw.stream()
                .filter(entry -> entry.size() == 2 && "usage_metadata".equals(entry.getFirst()))
                .findFirst()
                .map(entry -> {
                    try {
                        // entry.get(1) will be a LinkedHashMap when deserialized as Object
                        return mapper.convertValue(entry.get(1), UsageMetadata.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse(null);
    }
}
