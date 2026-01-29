package app.demo.neurade.domain.dtos;

import app.demo.neurade.infrastructures.llm.responses.WorkflowResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatResponseDTO {
    private String conversationId;
    private WorkflowResponse response;
}
