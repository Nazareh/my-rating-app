package com.turminaz.myratingapp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String MATCH_EXCHANGE = "spring-boot-exchange";

    public static final String MATCH_QUEUE = "spring-boot";

    @Bean
    Queue queue() {
        return new Queue(MATCH_QUEUE, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(MATCH_EXCHANGE);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("match.#");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(MATCH_QUEUE);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.addAllowedListPatterns("com.turminaz.myratingapp.*", "java.util.*", "java.time.*");
        return converter;
    }


}
