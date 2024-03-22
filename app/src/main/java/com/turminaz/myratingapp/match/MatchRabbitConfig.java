package com.turminaz.myratingapp.match;

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
class MatchRabbitConfig {

    static final String MATCH_EXCHANGE = "match-exchange";

    static final String MATCH_QUEUE = "match-queue";

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
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.addAllowedListPatterns("com.turminaz.myratingapp.*", "java.util.*", "java.time.*", "org.bson.types.ObjectId");
        return converter;
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
    MessageListenerAdapter playerListenerAdapter(PlayerReceiver receiver, SimpleMessageConverter converter) {
        var adapter = new MessageListenerAdapter(receiver);
        adapter.setMessageConverter(converter);
        return adapter;
    }
}
