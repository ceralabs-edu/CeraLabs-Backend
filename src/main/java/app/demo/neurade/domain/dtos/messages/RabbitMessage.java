package app.demo.neurade.domain.dtos.messages;

import java.util.UUID;

public interface RabbitMessage {
    UUID getId();
    MessageStatus getStatus();
    String getPrefix();
}
