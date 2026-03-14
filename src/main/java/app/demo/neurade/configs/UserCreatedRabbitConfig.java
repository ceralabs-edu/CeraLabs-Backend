package app.demo.neurade.configs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserCreatedRabbitConfig {
    public static final String USER_CREATED_EXCHANGE = "user.created.exchange";
    public static final String USER_CREATED_INSTANCE_CREATION_QUEUE = "user.created.instance.creation.queue";
    public static final String USER_CREATED_INSTANCE_CREATION_DLQ = "user.created.instance.creation.dlq";
    public static final String USER_CREATED_NOTIFICATION_QUEUE = "user.created.notification.queue";
    public static final String USER_CREATED_NOTIFICATION_DLQ = "user.created.notification.dlq";

    @Bean
    public FanoutExchange userCreatedExchange() {
        return new FanoutExchange(USER_CREATED_EXCHANGE);
    }

    @Bean
    public Queue userCreatedInstanceCreationQueue() {
        return QueueBuilder.durable(USER_CREATED_INSTANCE_CREATION_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", USER_CREATED_INSTANCE_CREATION_DLQ)
                .build();
    }

    @Bean
    public Queue userCreatedInstanceCreationDLQ() {
        return QueueBuilder.durable(USER_CREATED_INSTANCE_CREATION_DLQ).build();
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedInstanceCreationQueue, FanoutExchange userCreatedExchange) {
        return BindingBuilder.bind(userCreatedInstanceCreationQueue)
                .to(userCreatedExchange);
    }

    @Bean
    public Queue userCreatedNotificationQueue() {
        return QueueBuilder.durable(USER_CREATED_NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", USER_CREATED_NOTIFICATION_DLQ)
                .build();
    }

    @Bean
    public Queue userCreatedNotificationDLQ() {
        return QueueBuilder.durable(USER_CREATED_NOTIFICATION_DLQ).build();
    }

    @Bean
    public Binding userCreatedNotificationBinding(Queue userCreatedNotificationQueue, FanoutExchange userCreatedExchange) {
        return BindingBuilder.bind(userCreatedNotificationQueue)
                .to(userCreatedExchange);
    }
}

