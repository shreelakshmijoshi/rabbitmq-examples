package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.rabbitmq.RabbitMQClient;

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
        // also since the exchange is fanout we wouldn't require binding key for now
        // tho it does require binding of the exchange with the queue
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

    /**
     * Will create an exchange if not already declared. Will keep the exchange as it is if already declared
     * Additional configs can be added while declaring the queue
     */
    public void createQueue()
    {
        this.client.queueDeclare(
                new Util().getQueue(0),
                true,
                false,
                false,
                handler -> {
                    if (handler.succeeded()) {
                        System.out.println("Queue declared successfully");
                    } else {
                        System.out.println("Something went wrong while declaring queue : "+ handler.cause().getMessage());
                        handler.cause().printStackTrace();
                    }
                });
    }

    public void publishWithDeliveryTag(Vertx vertx)
    {
        this.client.basicPublishWithDeliveryTag(
                "",
                new Util().getQueue(0),
                null,
                Buffer.buffer(MESSAGE_TO_BE_SENT + " saying Hello :)"),
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
