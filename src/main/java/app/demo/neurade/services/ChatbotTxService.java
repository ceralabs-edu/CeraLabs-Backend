package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.ChatPrepareDTO;
import app.demo.neurade.domain.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ChatbotTxService {
    ChatPrepareDTO prepareChat(
            User user,
            UUID instanceId,
            String conversationId,
            String question,
            List<MultipartFile> files
    );

    void finalizeChat(
            User user,
            UUID instanceId,
            Long qaEntryId,
            String reply,
            long tokenUsed
    );
}
