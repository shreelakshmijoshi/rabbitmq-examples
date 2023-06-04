package org.example;

import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.List;

public class Main {
    public static Vertx vertx;
    public static List<String> queueList = List.of("Queue-1","Queue-2","Queue-3","Queue-4", "ac");

    public static void main(String[] args) {
        RabbitMQOptions config = new RabbitMQOptions();
        vertx = Vertx.vertx();
        config.setHost("localhost");
        RabbitMQClient rabbitMQClient = RabbitMQClient.create(vertx,config);
        rabbitMQClient.start(asyncResult -> {
            if(asyncResult.succeeded()){
                System.out.println("RabbitMQ successfully connected!");
                Publisher publisher = new Publisher(rabbitMQClient);
                // after the rabbitmq java client is connected to the rabbitmq server
                // we get to create exchanges, queues, bind queues to exchanges
                // publish messages to the exchanges, add consumer to consume the messages

                System.out.println("List of queues : " + queueList);
                publisher.createExchange();
                for(int index = 0; index < queueList.size(); index++)
                {
                    publisher.createQueue(index);
                    publisher.publishWithDeliveryTag(index);
                }
                System.exit(0);
            }
            else {
                System.out.println("Failed to connect to RabbitMQ " + asyncResult.cause().getMessage());
            }
        });

    }
}
