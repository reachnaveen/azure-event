package com.example.sender;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import java.util.Collections;

public class EventSender {

    public static void main(String[] args) {
        String connectionString = System.getenv("EVENTHUBS_CONNECTION_STRING");
        String eventHubName = System.getenv("EVENTHUBS_NAME");

        if (connectionString == null || eventHubName == null) {
            System.out.println("Please set the environment variables EVENTHUBS_CONNECTION_STRING and EVENTHUBS_NAME");
            return;
        }

        if (args.length == 0) {
            System.out.println("Please provide a message to send.");
            return;
        }

        EventHubProducerClient producer = new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .buildProducerClient();

        producer.send(Collections.singletonList(new EventData(args[0])));
        System.out.println("Message sent: " + args[0]);
        producer.close();
    }
}