package app.demo.neurade.domain.rabbitmq;

import java.util.UUID;

public interface RabbitJob {
    UUID getJobId();
    JobStatus getStatus();
    String getJobPrefix();
}
