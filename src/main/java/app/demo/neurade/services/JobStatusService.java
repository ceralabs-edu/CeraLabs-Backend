package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.messages.RabbitMessage;

import java.util.UUID;

public interface JobStatusService {
    void saveJob(RabbitMessage job);
    <T extends RabbitMessage> T getJob(UUID jobId, Class<T> jobClass);
}
