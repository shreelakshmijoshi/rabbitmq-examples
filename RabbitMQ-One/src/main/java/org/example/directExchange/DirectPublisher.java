package org.example.directExchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class DirectPublisher {

    private static final Logger LOGGER = Logger.getLogger("DirectPublisher.class");
    public static final String DEFAULT_QUEUE = "mobile";
    private static final String ROUTING_KEY = "mobile_rk";
    private static final String MESSAGE = "Which mobile has the best camera ? ";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()){

                channel.basicPublish("direct-exchange", ROUTING_KEY, null, MESSAGE.getBytes(StandardCharsets.UTF_8));
                LOGGER.info("Sent message : " + MESSAGE);
        }

//        channel.close();
//        connection.close();
    }

}
