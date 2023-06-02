package rabbitmq;

import io.vertx.core.Vertx;
import io.vertx.rabbitmq.QueueOptions;
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

    QueueOptions queueOptions = new QueueOptions();
    // maxInternalQueue size is the maximum number of messages a queue can hold
    queueOptions.setMaxInternalQueueSize(2).setKeepMostRecent(true);


    client
        .start()
        .onSuccess(
            handler -> {
              client.basicConsumer(
                  DEFAULT_QUEUE, new QueueOptions().setAutoAck(true),
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

  public void something()
  {

  }
}
// userID, Monitoring queueName , data
// /monitor -> REST API
// userId, monitoringQueueName from body of the API
// start a consumer for the queueName
// start a consumer with Future handler from RestAPIClass
// whenever we get the message,
// maintain map or class which has userID, queueName, data consumed
// keep appending to that map based on queueName
// return 201 created
// check how to get size from JSON or from String

// /status
// response will be userID, queueName
// respond with the data consumed
