package app.demo.neurade.services;

import app.demo.neurade.domain.rabbitmq.RabbitJob;

import java.util.UUID;

public interface JobStatusService {
    void saveJob(RabbitJob job);
    <T extends RabbitJob> T getJob(UUID jobId, Class<T> jobClass);
}
