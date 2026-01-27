package app.demo.neurade.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatResponseDTO {
    private String conversationId;
    private String message;
    private LocalDateTime timestamp;
}
