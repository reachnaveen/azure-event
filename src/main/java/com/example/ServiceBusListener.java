package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class ServiceBusListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBusListener.class);

    @Bean
    public Consumer<Message<String>> consumeServiceBusMessage() {
        return message -> {
            LOGGER.info("New Service Bus message received: '{}'", message.getPayload());
        };
    }
}