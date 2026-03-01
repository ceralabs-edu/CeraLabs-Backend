package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.ChatHistoryEntryDTO;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.chatbot.Conversation;
import app.demo.neurade.domain.dtos.messages.ChatbotChatMessage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ChatbotService {
    UUID enqueueChat(
            User user,
            UUID instanceId,
            String conversationId,
            String question,
            List<MultipartFile> files
    );

    ChatbotChatMessage getChatJobStatus(UUID jobId);

    List<ChatHistoryEntryDTO> getChatHistory(String conversationId);

    List<Conversation> getUserConversations(Long userId);
}
