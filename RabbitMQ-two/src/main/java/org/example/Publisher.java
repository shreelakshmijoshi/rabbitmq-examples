package org.example;

import io.vertx.core.buffer.Buffer;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;

import static org.example.Util.DEFAULT_EXCHANGE;
import static org.example.Util.DEFAULT_EXCHANGE_TYPE;

public class Publisher {
    private static final String MESSAGE_TO_BE_SENT = "This is a message from the vert.x rabbitmq client producer";
    private RabbitMQClient client;

    public Publisher(RabbitMQClient rabbitMQClient) {
        this.client = rabbitMQClient;
    }
    /**
     * Will create an exchange if not already declared. Will keep the exchange as it is if already declared
     */
    public void createExchange() {
        this.client.exchangeDeclare(
                DEFAULT_EXCHANGE,
                DEFAULT_EXCHANGE_TYPE,
                true,
                false,
                handler -> {
                    if (handler.succeeded()) {
                        System.out.println("Exchange successfully declared ");
                    } else {
                        System.out.println(
                                "Oh oh, something went wrong while declaring exchange : "
                                        + handler.cause().getMessage());
                    }
                });
    }

    public void bindQueueToExchange(int index)
    {
        this.client.exchangeBind(Main.queueList.get(index),DEFAULT_EXCHANGE,"routing.key." + (1+index),handler -> {
            if (handler.succeeded())
            {
                System.out.println("Binding for done for " + Main.queueList.get(index) + " with exchange " + DEFAULT_EXCHANGE);
            }
            else {
                System.out.println("Something went wrong" + handler.cause().getMessage());
                handler.cause().printStackTrace();
            }
        });
    }

    /**
     * Will create an exchange if not already declared. Will keep the exchange as it is if already declared
     * Additional configs can be added while declaring the queue
     */
    public void createQueue(int index)
    {
        System.out.println("Inside createQueue");
        this.client.queueDeclare(
                Main.queueList.get(index),
                true,
                false,
                false,
                handler -> {
                    if (handler.succeeded()) {
                        System.out.println("Queue declared successfully");
                        client.basicConsumer(Main.queueList.get(index),new QueueOptions(),ConsumerHandler -> {
                            if (handler.succeeded())
                            {
                                RabbitMQConsumer rmqConsumer = ConsumerHandler.result();
                                rmqConsumer.handler(
                                        rabbitMQMessage -> {
                                            System.out.println("Message received in consumer : " + rabbitMQMessage.body().toString());
                                        });
                            }
                            else
                            {
                                System.out.println("Something went wrong while creating consumer after declaring queue : "+ handler.cause().getMessage());
                                handler.cause().printStackTrace();
                            }
                        });
                    } else {
                        System.out.println("Something went wrong while declaring queue : "+ handler.cause().getMessage());
                        handler.cause().printStackTrace();
                    }
                });
    }

    public void publishWithDeliveryTag(int index)
    {
        System.out.println("inside publishWithDeliveryTag " + index);
        this.client.basicPublishWithDeliveryTag(
                DEFAULT_EXCHANGE,
                "routing.key." + (index+1),
                null,
                Buffer.buffer(MESSAGE_TO_BE_SENT + " saying Hello :)" + " " + (index+1)),
                deliveryTagHandler   -> {
                    long deliveryTag = deliveryTagHandler;
//          System.out.println("Delivery Tag is : " + deliveryTag);
                },
                publishHandler -> {
                    if (publishHandler.succeeded()) {
                        System.out.println("Publishing message to Queue succeed! ");
                    } else {
                        System.out.println(
                                "Publishing message to queue failed : " + publishHandler.cause().getMessage());
                        publishHandler.cause().printStackTrace();
                    }
                });
    }





}
