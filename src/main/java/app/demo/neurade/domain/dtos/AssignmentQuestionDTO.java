package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.assignment.QuestionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentQuestionDTO {
    private UUID id;

    @JsonProperty("order")
    private String questionKey;

    @JsonProperty("type")
    private QuestionType questionType;

    private String questionUrl;

    private List<String> optionUrls;

    private String answer;

    private String explainUrl;
}
