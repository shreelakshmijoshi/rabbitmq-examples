package org.example.fanoutexchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class FanoutPublisher {

    private static final Logger LOGGER = Logger.getLogger("FanoutPublisher.class");
    public static final String DEFAULT_QUEUE_1 = "mobile";
    public static final String DEFAULT_QUEUE_2 = "ac";

    private static final String ROUTING_KEY = "";
    private static final String MESSAGE = "What is the price of the electronic appliance";
    private static final String EXCHANGE = "fanout-exchange";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()){

                channel.basicPublish(EXCHANGE, ROUTING_KEY, null, MESSAGE.getBytes(StandardCharsets.UTF_8));
                LOGGER.info("Sent message : " + MESSAGE);
        }

//        channel.close();
//        connection.close();
    }

}
