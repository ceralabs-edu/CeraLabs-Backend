package app.demo.neurade.domain.rabbitmq;

import app.demo.neurade.domain.dtos.ChatResponseDTO;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatbotChatJob {
    private UUID jobId;
    private JobStatus status;
    private Long userId;
    private UUID instanceId;
    private String conversationId;
    private Long qaEntryId;
    private String apiKey;
    private String question;
    private List<String> fileUrls;
    private String errorMessage;
    private ChatResponseDTO response;
}
