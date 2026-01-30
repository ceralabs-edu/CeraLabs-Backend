package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.ChatHistoryEntryDTO;
import app.demo.neurade.domain.dtos.ChatResponseDTO;
import app.demo.neurade.domain.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ChatbotService {
    ChatResponseDTO chat(
            User user,
            UUID instanceId,
            String conversationId,
            String question,
            List<MultipartFile> files
    );

    List<ChatHistoryEntryDTO> getChatHistory(String conversationId);
}
