package app.demo.neurade.domain.dtos;

import app.demo.neurade.infrastructures.chatbot_llm.responses.WorkflowResponse;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    private String conversationId;
    private WorkflowResponse response;
}
