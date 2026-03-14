package app.demo.neurade.configs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatbotRabbitConfig {
    public static final String CHATBOT_QUEUE = "chatbot.processing.queue";
    public static final String CHATBOT_DLQ = "chatbot.processing.dlq";
    public static final String CHATBOT_EXCHANGE = "chatbot.processing.exchange";
    public static final String CHATBOT_ROUTING_KEY = "chatbot.process";

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
}

