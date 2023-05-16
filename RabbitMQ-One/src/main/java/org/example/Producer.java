package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class Producer {

    private static final Logger LOGGER = Logger.getLogger("Main.class");
    public static final String DEFAULT_QUEUE = "Queue-1";
    private static final String ROUTING_KEY = "A";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try( Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()){
            channel.queueDeclare(DEFAULT_QUEUE,true,false,false,null);
            //using the channel object, we can publish the messages to the queue
            // ideally we need an exchange attached to a channel
            //   .basicPublish(exchange, routingKey, properties, message_body);
            //        Routing key = queue name
            String message = "Sending this secret message from the producer";
            channel.basicPublish("exchange_demo", ROUTING_KEY, null, message.getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Sent message : " + message);
        }

//        channel.close();
//        connection.close();
    }

}