package app.demo.neurade.infrastructures.llm.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponse {

//    private List<String> images;
//    private List<List<String>> queries;
    private Guardian guardian;

    @JsonProperty("assistant")
    private Assistant assistant;

    @JsonProperty("assistant_raw")
    private AssistantRaw assistantRaw;

    @JsonProperty("guardian_raw")
    private GuardianRaw guardianRaw;

    @Getter
    @Setter
    public static class Guardian {
        @JsonProperty("problem_type")
        private List<String> problemType;
        private String difficulty;
        private String route;
        private String reply;
    }

    @Getter
    @Setter
    public static class Assistant {
        private String approach;
        private List<SolutionStep> solution;

        @JsonProperty("final_answer")
        private String finalAnswer;
    }

    @Getter
    @Setter
    public static class SolutionStep {
        private String title;
        private String solving;
        private String explanation;
    }

    @Getter
    @Setter
    public static class AssistantRaw {
        private String content;

        @JsonProperty("usage_metadata")
        private UsageMetadata usageMetadata;
    }

    @Getter
    @Setter
    public static class GuardianRaw {
        private String content;

        @JsonProperty("usage_metadata")
        private UsageMetadata usageMetadata;
    }

    @Getter
    @Setter
    public static class UsageMetadata {
        @JsonProperty("input_tokens")
        private long inputTokens;

        @JsonProperty("output_tokens")
        private long outputTokens;

        @JsonProperty("total_tokens")
        private long totalTokens;
    }
}
