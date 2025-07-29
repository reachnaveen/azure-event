package com.example;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.ServiceBusMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RestController
public class AzureEventhubListenerApplication {

    @Value("${spring.cloud.azure.servicebus.connection-string}")
    private String connectionString;

    @Value("${spring.cloud.stream.bindings.consumeServiceBusMessage-in-0.destination}")
    private String queueName;

    @PostMapping("/publish")
    public String publishMessage(@RequestBody String message) {
        try (ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient()) {
            senderClient.sendMessage(new ServiceBusMessage(message));
        }
        return "Message published";
    }
    @GetMapping("/health")
    public String healthCheck() {
        return "Service is running";
    }
    public static void main(String[] args) {
        SpringApplication.run(AzureEventhubListenerApplication.class, args);
    }

    // @GetMapping("/consume")
    // public String consumeMessage() {
    //     try (com.azure.messaging.servicebus.ServiceBusReceiverClient receiverClient = new ServiceBusClientBuilder()
    //             .connectionString(connectionString)
    //             .receiver()
    //             .queueName(queueName)
    //             .buildClient()) {
    //         com.azure.messaging.servicebus.ServiceBusReceivedMessage receivedMessage = receiverClient.receiveMessages(1).stream().findFirst().orElse(null);
    //         if (receivedMessage != null) {
    //             String body = receivedMessage.getBody().toString();
    //             receiverClient.complete(receivedMessage.getLockToken());
    //             return "Received message: " + body;
    //         } else {
    //             return "No messages available";
    //         }
    //     }
    // }
}
