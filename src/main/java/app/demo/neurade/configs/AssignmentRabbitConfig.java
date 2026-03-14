package app.demo.neurade.configs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssignmentRabbitConfig {
    public static final String ASSIGNMENT_QUEUE = "assignment.judge.queue";
    public static final String ASSIGNMENT_DLQ = "assignment.judge.dlq";
    public static final String ASSIGNMENT_EXCHANGE = "assignment.judge.exchange";
    public static final String ASSIGNMENT_ROUTING_KEY = "assignment.judge";

    @Bean
    public Queue assignmentQueue() {
        return QueueBuilder.durable(ASSIGNMENT_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", ASSIGNMENT_DLQ)
                .build();
    }

    @Bean
    public Queue assignmentDLQ() {
        return QueueBuilder.durable(ASSIGNMENT_DLQ).build();
    }

    @Bean
    public Exchange assignmentExchange() {
        return new DirectExchange(ASSIGNMENT_EXCHANGE);
    }

    @Bean
    public Binding assignmentBinding(Queue assignmentQueue, Exchange assignmentExchange) {
        return BindingBuilder.bind(assignmentQueue)
                .to(assignmentExchange)
                .with(ASSIGNMENT_ROUTING_KEY)
                .noargs();
    }
}

