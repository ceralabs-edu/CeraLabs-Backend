package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.ChatResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatbotService {
    ChatResponseDTO chat(
            String conversationId,
            String question,
            List<MultipartFile> files
    );
}
