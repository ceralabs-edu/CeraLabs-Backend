package app.demo.neurade.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    public static final String CHATBOT_QUEUE = "chatbot.processing.queue";
    public static final String CHATBOT_DLQ = "chatbot.processing.dlq";
    public static final String CHATBOT_EXCHANGE = "chatbot.processing.exchange";
    public static final String CHATBOT_ROUTING_KEY = "chatbot.process";

    public static final String ASSIGNMENT_QUEUE = "assignment.judge.queue";
    public static final String ASSIGNMENT_DLQ = "assignment.judge.dlq";
    public static final String ASSIGNMENT_EXCHANGE = "assignment.judge.exchange";
    public static final String ASSIGNMENT_ROUTING_KEY = "assignment.judge";

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public Queue chatbotQueue() {
        return QueueBuilder.durable(CHATBOT_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", CHATBOT_DLQ)
                .build();
    }

    @Bean
    public Queue chatbotDLQ() {
        return QueueBuilder.durable(CHATBOT_DLQ).build();
    }

    @Bean
    public Exchange chatbotExchange() {
        return new DirectExchange(CHATBOT_EXCHANGE);
    }

    @Bean
    public Binding chatbotBinding(Queue chatbotQueue, Exchange chatbotExchange) {
        return BindingBuilder.bind(chatbotQueue)
                .to(chatbotExchange)
                .with(CHATBOT_ROUTING_KEY)
                .noargs();
    }

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
