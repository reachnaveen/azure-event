package com.example;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
class MessageConsumerTest {

    // @Container
    // static GenericContainer<?> serviceBusContainer = new GenericContainer<>("mcr.microsoft.com/azure-service-bus/amqp-service:latest")
    //         .withExposedPorts(5672)
    //         .withEnv("AZURE_SERVICE_BUS_NAMESPACE", "test-namespace");

    // @SpyBean
    // private ServiceBusListener serviceBusListener;

    // @DynamicPropertySource
    // static void dynamicProperties(DynamicPropertyRegistry registry) {
    //     String connectionString = String.format("amqp://localhost:%d", serviceBusContainer.getMappedPort(5672));
    //     registry.add("spring.cloud.azure.servicebus.connection-string", () -> connectionString);
    //     registry.add("spring.cloud.stream.bindings.consumeServiceBusMessage-in-0.destination", () -> "test-queue");
    // }

    // @Test
    // void testConsumeServiceBusMessage() {
    //     String connectionString = String.format("amqp://localhost:%d", serviceBusContainer.getMappedPort(5672));
    //     ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
    //             .connectionString(connectionString)
    //             .sender()
    //             .queueName("test-queue")
    //             .buildClient();

    //     senderClient.sendMessage(new ServiceBusMessage("Hello, World!"));

    //     verify(serviceBusListener, timeout(Duration.ofSeconds(10).toMillis()).times(1))
    //             .consumeServiceBusMessage();
    // }
}
