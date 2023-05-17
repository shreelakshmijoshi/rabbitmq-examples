package org.example.headersExchange;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class HeadersExchangePublisher {
    private static final Logger LOGGER = Logger.getLogger("HeadersExchangePublisher.class");
    private static final String ROUTING_KEY = "";
    private static final String MESSAGE = "Message for mobile and tv";
    private static final String EXCHANGE = "header-exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()){
            Map<String, Object> map = new HashMap<>();
            map.put("item1","mobile");
            map.put("item2","television");
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties()
                    .builder()
                    .headers(map);
            AMQP.BasicProperties basicProperties = builder.build();

            channel.basicPublish(EXCHANGE, ROUTING_KEY, basicProperties, MESSAGE.getBytes(StandardCharsets.UTF_8));
                LOGGER.info("Sent message : " + MESSAGE);
        }
    }

}
