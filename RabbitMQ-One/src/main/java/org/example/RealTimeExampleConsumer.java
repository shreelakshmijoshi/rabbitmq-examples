package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RealTimeExampleConsumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        DeliverCallback callback = ((consumerTag, body) -> {
            String message = new String(body.getBody());
            JSONObject jsonObject1 = new JSONObject(message);
            System.out.println("Received this JSON Object : " + jsonObject1);
        });

        channel.basicConsume(RealTimeExample.QUEUE_NAME, true, callback, consumerTag -> {
        });
    }
}
