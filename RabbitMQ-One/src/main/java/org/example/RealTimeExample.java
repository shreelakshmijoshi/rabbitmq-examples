package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RealTimeExample {
    public static final String QUEUE_NAME = "Queue-for-json-message";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from_date", "01-Jan-2019")
                .put("to_date", "09-Jan-2019")
                .put("email","user@gmail.com")
                .put("query", "SELECT * FROM DB_TABLE");

        channel.basicPublish("", QUEUE_NAME,null, jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        channel.close();
        connection.close();
    }
}
