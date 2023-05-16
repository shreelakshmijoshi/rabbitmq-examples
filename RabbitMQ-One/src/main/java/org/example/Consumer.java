package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import static org.example.Producer.DEFAULT_QUEUE;

public class Consumer {
    private static final Logger LOGGER = Logger.getLogger("Consumer.class");
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            LOGGER.info("The message received is : " + message);
        };
        channel.basicConsume(DEFAULT_QUEUE,true,deliverCallback,consumerTag -> {});
    }
}
