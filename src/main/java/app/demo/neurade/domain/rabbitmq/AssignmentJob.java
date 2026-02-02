package app.demo.neurade.domain.rabbitmq;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssignmentJob implements RabbitJob {
    private UUID jobId;
    private Long userId;
    private String answerImageUrl;
    private String apiKey;
    private UUID questionId;
    private String errorMessage;
    private JobStatus status;
    private String response;

    @Override
    @JsonIgnore
    public String getJobPrefix() {
        return "assignment:job:";
    }
}
