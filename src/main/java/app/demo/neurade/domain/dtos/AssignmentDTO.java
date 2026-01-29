package app.demo.neurade.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class AssignmentDTO {
    private UUID id;
    private Long classId;
    private String title;
    private String description;
    private LocalDateTime deadline;
}
