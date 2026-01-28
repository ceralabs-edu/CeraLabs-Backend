package app.demo.neurade.domain.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Chat request payload")
public class ChatRequest {
    @Schema(description = "AI instance ID", example = "1")
    private UUID instanceId;

    @Schema(description = "Conversation UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String conversationId;

    @Schema(description = "User's question to the chatbot, must be math-related", example = "What is the integral of x^2?")
    private String question;
}
