package rabbitmq;

import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.concurrent.Flow;

public class RabbitClient {
//  public static final String AMQP_URL = "amqp://guest:guest@localhost:-1/";
  public static Vertx vertx;
  public static void main(String[] args){
    RabbitMQOptions config = new RabbitMQOptions();
    vertx = Vertx.vertx();
//    config.setUri(AMQP_URL);
    config.setHost("localhost");
    config.setAutomaticRecoveryEnabled(false);
    config.setReconnectAttempts(10);
    config.setReconnectInterval(500);
    RabbitMQClient rabbitMQClient = RabbitMQClient.create(vertx,config);


    // Connect
    rabbitMQClient.start(asyncResult -> {
      if(asyncResult.succeeded()){
        System.out.println("RabbitMQ successfully connected!");
        VertxPublisher vertxPublisher = new VertxPublisher(rabbitMQClient);
        // after the rabbitmq java client is connected to the rabbitmq server
        // we get to create exchanges, queues, bind queues to exchanges
        // publish messages to the exchanges, add consumer to consume the messages

        vertxPublisher.createExchange();
        vertxPublisher.createQueue();
        vertxPublisher.publishWithDeliveryTag(vertx);
        System.exit(0);
      }
      else {
        System.out.println("Failed to connect to RabbitMQ " + asyncResult.cause().getMessage());
      }
    });

  }
}
