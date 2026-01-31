package app.demo.neurade.domain.rabbitmq;

import app.demo.neurade.domain.models.User;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssignmentJob {
    private User user;
}
