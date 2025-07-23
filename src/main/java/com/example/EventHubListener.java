package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class EventHubListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHubListener.class);

    @Bean
    public Consumer<Message<String>> consume() {
        return message -> {
            LOGGER.info("New message received: '{}'", message.getPayload());
        };
    }
}