package app.demo.neurade.infrastructures.chatbot_llm.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponse {

    private static final ObjectMapper mapper = new ObjectMapper();

    private Guardian guardian;
    private Assistant assistant;
    private Judge judge;

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
    }

    @Getter
    public static class Assistant {
        @Getter
        public static class AssistantResponse {
            @Getter
            public static class SolutionStep {
                private String title;
                private String solving;
                private String explanation;
            }
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
    }

    @Getter
    public static class Judge {
        @Getter
        public static class JudgeResponse {
            private float score;
            private String detailed_feedback;

            @Override
            public String toString() {
                try {
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
                } catch (Exception e) {
                    return "{\"error\":\"Could not serialize Judge to JSON\"}";
                }
            }
        }

        @JsonProperty("response")
        private JudgeResponse response;

        @JsonProperty("response_raw")
        @JsonIgnore
        private List<List<Object>> responseRaw;
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

    public UsageMetadata totalUsageMetadata() {
        long totalInput = 0, totalOutput = 0, totalTokens = 0;

        List<List<List<Object>>> allRaws = new ArrayList<>();
        if (guardian  != null) allRaws.add(guardian.getResponseRaw());
        if (assistant != null) allRaws.add(assistant.getResponseRaw());
        if (judge     != null) allRaws.add(judge.getResponseRaw());

        for (List<List<Object>> raw : allRaws) {
            UsageMetadata usage = extractUsageMetadata(raw);
            if (usage != null) {
                totalInput  += usage.getInputTokens();
                totalOutput += usage.getOutputTokens();
                totalTokens += usage.getTotalTokens();
            }
        }

        UsageMetadata total = new UsageMetadata();
        total.inputTokens  = totalInput;
        total.outputTokens = totalOutput;
        total.totalTokens  = totalTokens;
        return total;
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

    @Override
    public String toString() {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            return "{\"error\":\"Could not serialize WorkflowResponse to JSON\", \"message\":\"" + e.getMessage() + "\"}";
        }
    }
}
