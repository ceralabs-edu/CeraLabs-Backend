package app.demo.neurade.services.impl;

import app.demo.neurade.domain.rabbitmq.ChatbotChatJob;
import app.demo.neurade.services.JobStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobStatusServiceImpl implements JobStatusService {

    private static final String CHATBOT_CHAT_JOB_PREFIX = "chatbot:chat:job:";
    private static final Duration TTL = Duration.ofHours(1);
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveChatbotChatJob(ChatbotChatJob job) {
        String key = CHATBOT_CHAT_JOB_PREFIX + job.getJobId();
        redisTemplate.opsForValue().set(key, job, TTL);
    }

    @Override
    public ChatbotChatJob getChatbotChatJob(UUID jobId) {
        String key = CHATBOT_CHAT_JOB_PREFIX + jobId;
        return (ChatbotChatJob) redisTemplate.opsForValue().get(key);
    }
}
