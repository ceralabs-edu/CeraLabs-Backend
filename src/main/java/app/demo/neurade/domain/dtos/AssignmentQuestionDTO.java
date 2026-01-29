package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.assignment.QuestionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AssignmentQuestionDTO {
    @JsonProperty("order")
    private String questionKey;

    @JsonProperty("type")
    private QuestionType questionType;

    private String questionUrl;

    private String answer;

    private String explainUrl;
}
