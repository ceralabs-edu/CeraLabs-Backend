package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.ChatResponseDTO;
import app.demo.neurade.services.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    @Override
    public ChatResponseDTO chat(String conversationId, String question, List<MultipartFile> files) {
        // TODO: Implement chatbot logic here
        return null;
    }
}
