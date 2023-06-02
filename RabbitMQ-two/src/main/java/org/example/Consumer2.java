package org.example;

import io.vertx.core.Vertx;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.RabbitMQOptions;

public class Consumer2 {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        RabbitMQOptions rabbitMQOptions = new RabbitMQOptions();
        rabbitMQOptions.setHost("localhost");
        RabbitMQClient rabbitMQClient = RabbitMQClient.create(vertx,rabbitMQOptions);
        QueueOptions queueOptions = new QueueOptions();
        queueOptions.setAutoAck(true);

// user will tell from which

        rabbitMQClient.start().onSuccess(handler -> {
            vertx.setPeriodic(1000, periodicHandler -> {
                for(String queue: Main.queueList)
                {
                    consume(rabbitMQClient, queueOptions,queue);
                }
            });
        });

    }

    private static void consume(RabbitMQClient rabbitMQClient, QueueOptions queueOptions,String queue) {
        rabbitMQClient.basicConsumer(queue, queueOptions, rabbitMQConsumerAsyncResult -> {
            if(rabbitMQConsumerAsyncResult.succeeded()){
                System.out.println("The consumer is created! : ");
                System.out.println("Consumer Tag : " + rabbitMQConsumerAsyncResult.result().consumerTag());
                System.out.println("Queue Name : " + rabbitMQConsumerAsyncResult.result().queueName());
                RabbitMQConsumer rmqConsumer = rabbitMQConsumerAsyncResult.result();
                rmqConsumer.handler(
                        rabbitMQMessage -> {
                            System.out.println("Message received in consumer : " + rabbitMQMessage.body().toString());
                        });
                rmqConsumer.cancel();
            }
            else
            {
                System.out.println("Failure while creating the consumer " + rabbitMQConsumerAsyncResult.cause().getMessage());
                rabbitMQConsumerAsyncResult.cause().printStackTrace();
            }
        });

    }
}
