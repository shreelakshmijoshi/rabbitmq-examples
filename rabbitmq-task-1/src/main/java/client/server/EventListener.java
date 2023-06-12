package client.server;

import com.rabbitmq.client.*;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class EventListener {
  public void listen() throws IOException, TimeoutException {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    Connection connection = connectionFactory.newConnection();
    Channel channel = connection.createChannel();
    String queue = "Some-queue-111";
    channel.queueDeclare(queue, true, false, false, null);
    channel.queueBind(queue, "amq.rabbitmq.event", "queue.*");

    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        String event = envelope.getRoutingKey();
        Map<String, Object> headers = properties.getHeaders();
        String name = headers.get("name").toString();
        String vhost = headers.get("vhost").toString();
        var argument = headers.get("arguments");
        String arg = headers.get("arguments").toString();
        System.out.println("8888888888");

//        JsonObject jsonObject = new JsonObject(arg);
//        System.out.println("JsonObject : " + jsonObject.encode());
        System.out.println();
        System.out.println("Arg : " + arg);
        System.out.println("Monitoring required : " + argument);
        System.out.println();

        System.out.println("headers : " + headers);
        System.out.println("Name in header " + name + " vhost is " + vhost);
        System.out.println("Event : " + event);
        System.out.println("Properties : " + properties);
// header
        // size
        // userID
        // monitoring is required
        // queueName -> From RMQ Exchange Plugin

        if (event.equals("queue.created")) {
          boolean durable = (Boolean) headers.get("durable");
          String durableString = durable ? " (durable)" : " (transient)";
          System.out.println("Created: " + name + " in " + vhost + durableString);
        } else /* queue.deleted is the only other possibility */ {
          System.out.println("Deleted: " + name + " in " + vhost);
        }
      }

    };
    channel.basicConsume(queue, true, consumer);
    System.out.println("QUEUE EVENTS");
    System.out.println("============\n");
  }

  public Future<Boolean> listen1() throws IOException, TimeoutException {
    Promise<Boolean> promise = Promise.promise();
    ConnectionFactory connectionFactory = new ConnectionFactory();
    Connection connection = connectionFactory.newConnection();
    Channel channel = connection.createChannel();
    String queue = "Some-queue-111";
    channel.queueDeclare(queue, true, false, false, null);
    channel.queueBind(queue, "amq.rabbitmq.event", "queue.*");

    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        String event = envelope.getRoutingKey();
        Map<String, Object> headers = properties.getHeaders();
        String name = headers.get("name").toString();
        String vhost = headers.get("vhost").toString();
        var argument = headers.get("arguments");
        String arg = headers.get("arguments").toString();
        System.out.println("8888888888");

//        JsonObject jsonObject = new JsonObject(arg);
//        System.out.println("JsonObject : " + jsonObject.encode());
        System.out.println();
        System.out.println("Arg : " + arg);
        System.out.println("Monitoring required : " + argument);
        System.out.println();

        System.out.println("headers : " + headers);
        System.out.println("Name in header " + name + " vhost is " + vhost);
        System.out.println("Event : " + event);
        System.out.println("Properties : " + properties);
// header
        // size
        // userID
        // monitoring is required
        // queueName -> From RMQ Exchange Plugin

        if (event.equals("queue.created")) {
          boolean durable = (Boolean) headers.get("durable");
          String durableString = durable ? " (durable)" : " (transient)";
          System.out.println("Created: " + name + " in " + vhost + durableString);
        } else /* queue.deleted is the only other possibility */ {
          System.out.println("Deleted: " + name + " in " + vhost);
        }
      }

    };
    promise.complete(true);
//    promise.fail(resultHandler.cause().getMessage());
    channel.basicConsume(queue, true, consumer);
    System.out.println("QUEUE EVENTS");
    System.out.println("============\n");
    return promise.future();

  }
}
