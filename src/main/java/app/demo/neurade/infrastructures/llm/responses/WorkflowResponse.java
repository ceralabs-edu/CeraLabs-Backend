package app.demo.neurade.infrastructures.llm.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponse {

    private List<String> images;
    private List<List<String>> queries;
    private Guardian guardian;

    @JsonProperty("assistant")
    private Assistant assistant;

    @Getter
    @Setter
    public static class Guardian {
        @JsonProperty("problem_type")
        private List<String> problemType;
        private String difficulty;
        private String route;
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
}
