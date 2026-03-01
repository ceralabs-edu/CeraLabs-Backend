package app.demo.neurade.services.consumers;

import app.demo.neurade.configs.RabbitMQConfig;
import app.demo.neurade.domain.dtos.messages.UserCreatedMessage;
import app.demo.neurade.services.AIPackageInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer that listens to user created events from RabbitMQ.
 * This is where you implement post-user-creation operations like:
 * - Creating a free personal AI instance
 * - Sending welcome emails
 * - Creating default user settings
 * - Adding to analytics/CRM
 * Benefits of using RabbitMQ over simple events:
 * - Messages persist even if the app crashes
 * - Can be processed by separate microservices
 * - Built-in retry logic with DLQ (Dead Letter Queue)
 * - Better scalability and reliability
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedConsumer {

    private final AIPackageInstanceService aiPackageInstanceService;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void handleUserCreated(UserCreatedMessage message) {
        log.info("Received user created message for user: {} (source: {})",
                message.getEmail(), message.getSource());

        try {
            switch (message.getSource()) {
                case REGISTRATION -> handleRegistrationUser(message);
                case OAUTH        -> handleOAuthUser(message);
                case ADMIN_CREATION -> handleAdminCreatedUser(message);
            }
            log.info("Successfully processed user created message for: {}", message.getEmail());
        } catch (Exception e) {
            log.error("Error processing user created message for: {}", message.getEmail(), e);
            throw e;
        }
    }

    private void handleRegistrationUser(UserCreatedMessage message) {
        log.info("Processing registration user: {}", message.getEmail());
        createFreeAIInstance(message);
    }

    private void handleOAuthUser(UserCreatedMessage message) {
        log.info("Processing OAuth user: {}", message.getEmail());
        createFreeAIInstance(message);
    }

    private void handleAdminCreatedUser(UserCreatedMessage message) {
        log.info("Processing admin-created user: {}", message.getEmail());
        createFreeAIInstance(message);
    }

    private void createFreeAIInstance(UserCreatedMessage message) {
        log.info("Creating free AI instance for user: {}", message.getEmail());
        aiPackageInstanceService.createFreeInstanceForUser(message.getUserId());
        log.info("Free AI instance created for user: {}", message.getEmail());
    }
}
