package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.dtos.messages.MessageStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotChatJobDTO {
    private UUID jobId;
    private MessageStatus status;
    private String errorMessage;
    private ChatResponseDTO response;
}
