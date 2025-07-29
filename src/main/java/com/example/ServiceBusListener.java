package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.azure.spring.messaging.servicebus.support.ServiceBusMessageHeaders;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import org.springframework.messaging.Message;


@Configuration
public class ServiceBusListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBusListener.class);

    @Bean
    public Consumer<Message<String>> consumeServiceBusMessage() {
        return message -> {
            LOGGER.info("New Service Bus message received: '{}'", message.getPayload());

            // Use string keys for headers
            ServiceBusReceivedMessage sbMessage = message.getHeaders()
                .get("azure_servicebus_receivedMessage", ServiceBusReceivedMessage.class);
            ServiceBusReceiverClient receiverClient = message.getHeaders()
                .get("azure_servicebus_receiverClient", ServiceBusReceiverClient.class);

            if (sbMessage != null && receiverClient != null) {
                receiverClient.complete(sbMessage);
                LOGGER.info("Service Bus message completed (acknowledged).");
            } else {
                LOGGER.warn("Could not complete message: ServiceBusReceivedMessage or ServiceBusReceiverClient not found in headers.");
            }
        };
    }
}

