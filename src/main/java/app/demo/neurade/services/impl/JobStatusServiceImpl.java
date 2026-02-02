package app.demo.neurade.services.impl;

import app.demo.neurade.domain.rabbitmq.RabbitJob;
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
    public void saveJob(RabbitJob job) {
        String key = job.getJobPrefix() + job.getJobId();
        redisTemplate.opsForValue().set(key, job, TTL);
    }

    @Override
    public <T extends RabbitJob> T getJob(UUID jobId, Class<T> jobClass) {
        String key;
        try {
            T tempInstance = jobClass.getDeclaredConstructor().newInstance();
            key = tempInstance.getJobPrefix() + jobId;
            return jobClass.cast(redisTemplate.opsForValue().get(key));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create job instance: " + e.getMessage(), e);
        }
    }


}
