package app.demo.neurade.services;

import app.demo.neurade.domain.rabbitmq.ChatbotChatJob;

import java.util.UUID;

public interface JobStatusService {
    void saveChatbotChatJob(ChatbotChatJob job);
    ChatbotChatJob getChatbotChatJob(UUID jobId);
}
