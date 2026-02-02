package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.rabbitmq.JobStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentJobDTO {
    private UUID jobId;
    private JobStatus status;
    private String errorMessage;
    private String response;
}
