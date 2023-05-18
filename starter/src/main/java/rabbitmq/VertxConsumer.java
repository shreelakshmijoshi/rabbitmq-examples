package rabbitmq;

import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.concurrent.atomic.AtomicReference;

import static rabbitmq.VertxPublisher.DEFAULT_QUEUE;

public class VertxConsumer {
  public static void main(String[] args){
    RabbitMQOptions rabbitMQOptions = new RabbitMQOptions();
    rabbitMQOptions.setHost("localhost");
    RabbitMQClient client = RabbitMQClient.create(Vertx.vertx(),rabbitMQOptions);
    AtomicReference<RabbitMQConsumer> consumer = new AtomicReference<>();

    client
        .start()
        .onSuccess(
            handler -> {
              client.basicConsumer(
                  DEFAULT_QUEUE,
                  resultHandler -> {
                    if (resultHandler.succeeded()) {

                      System.out.println("The consumer is created! : ");
                      System.out.println("Consumer Tag : " + resultHandler.result().consumerTag());
                      System.out.println("Queue Name : " + resultHandler.result().queueName());
                      RabbitMQConsumer rmqConsumer = resultHandler.result();
                      rmqConsumer.handler(
                          rabbitMQMessage -> {
                            System.out.println("Message received in consumer : " + rabbitMQMessage.body().toString());
                          });
                    } else {
                      System.out.println(
                          "Failure while receiving the message "
                              + resultHandler.cause().getMessage());
                      resultHandler.cause().printStackTrace();
                    }
                  });
            });
  }
}
