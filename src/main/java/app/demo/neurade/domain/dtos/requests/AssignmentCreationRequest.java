package app.demo.neurade.domain.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentCreationRequest {
    private String title;
    private String description;
    private LocalDateTime deadline;
}
