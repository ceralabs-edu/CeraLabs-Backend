package app.demo.neurade.domain.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private UUID id;
    private Long classId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private List<AssignmentQuestionDTO> questions;
}
