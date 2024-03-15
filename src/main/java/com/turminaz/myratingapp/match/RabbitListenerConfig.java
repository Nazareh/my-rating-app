package com.turminaz.myratingapp.match;

import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListenerConfig {

    @Bean
    MessageListenerAdapter matchListenerAdapter(MatchReceiver receiver, SimpleMessageConverter converter) {
        var adapter = new MessageListenerAdapter(receiver);
        adapter.setMessageConverter(converter);
        return adapter;

    }
}
