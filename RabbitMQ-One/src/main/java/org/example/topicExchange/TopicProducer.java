package org.example.topicExchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class TopicProducer {
    private static final Logger LOGGER = Logger.getLogger("TopicProducer.class");
    private static final String ROUTING_KEY = "tv.mobile.ac";
    private static final String MESSAGE = "Secret message for mobile and ac";
    private static final String EXCHANGE = "topic-exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()){

                channel.basicPublish(EXCHANGE, ROUTING_KEY, null, MESSAGE.getBytes(StandardCharsets.UTF_8));
                LOGGER.info("Sent message : " + MESSAGE);
        }
    }

}
