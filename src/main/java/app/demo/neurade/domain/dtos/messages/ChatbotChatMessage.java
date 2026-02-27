package app.demo.neurade.domain.dtos.messages;

import app.demo.neurade.domain.dtos.ChatResponseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatbotChatMessage implements RabbitMessage {
    private UUID id;
    private MessageStatus status;
    private Long userId;
    private UUID instanceId;
    private String conversationId;
    private Long qaEntryId;
    private String apiKey;
    private String question;
    private List<String> fileUrls;
    private String errorMessage;
    private ChatResponseDTO response;

    @Override
    @JsonIgnore
    public String getPrefix() {
        return "chatbot:chat:";
    }
}
