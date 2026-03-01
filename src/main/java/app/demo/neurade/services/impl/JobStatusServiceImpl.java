package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.messages.RabbitMessage;
import app.demo.neurade.services.JobStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobStatusServiceImpl implements JobStatusService {

    private static final Duration TTL = Duration.ofHours(1);
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveJob(RabbitMessage job) {
        String key = job.getPrefix() + job.getId();
        redisTemplate.opsForValue().set(key, job, TTL);
    }

    @Override
    public <T extends RabbitMessage> T getJob(UUID jobId, Class<T> jobClass) {
        String key;
        try {
            T tempInstance = jobClass.getDeclaredConstructor().newInstance();
            key = tempInstance.getPrefix() + jobId;
            return jobClass.cast(redisTemplate.opsForValue().get(key));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create job instance: " + e.getMessage(), e);
        }
    }


}
