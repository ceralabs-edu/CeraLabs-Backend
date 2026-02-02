package app.demo.neurade.domain.rabbitmq;

public enum JobStatus {
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED
}
