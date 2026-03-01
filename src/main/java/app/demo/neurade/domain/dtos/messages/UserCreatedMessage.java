package app.demo.neurade.domain.dtos.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * Message sent to RabbitMQ when a new user is created.
 * This allows other services/consumers to react to user creation events.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedMessage implements Serializable, RabbitMessage {

    private UUID id;
    private MessageStatus status;
    private Long userId;
    private String email;
    private Short roleId;
    private UserCreationSource source;

    @Override
    @JsonIgnore
    public String getPrefix() {
        return "user:created:";
    }

    /**
     * Enum to distinguish how the user was created
     */
    public enum UserCreationSource {
        REGISTRATION,  // Normal registration form
        OAUTH,         // OAuth (Google, etc.)
        ADMIN_CREATION // Created by admin
    }
}

