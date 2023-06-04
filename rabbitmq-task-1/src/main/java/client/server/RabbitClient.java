package client.server;


import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.*;

import java.util.List;

public class RabbitClient {
  private RabbitMQClient client;
  private RabbitMQOptions rabbitMQOptions;
  private Monitor monitor;

  public RabbitClient(Vertx vertx) {
    rabbitMQOptions = new RabbitMQOptions();
    rabbitMQOptions.setHost("localhost");
    monitor = new Monitor();
    // connect to RMQ Client
    client = RabbitMQClient.create(vertx, rabbitMQOptions);
//    startConsumer(client);
  }

//  private void startConsumer(RabbitMQClient rabbitMQClient) {
//    rabbitMQClient.start(voidAsyncResult -> {
//      if (voidAsyncResult.succeeded()) {
//        System.out.println("RabbitMQ connection successful");
//      } else {
//        System.out.println("RabbitMQ connection failure");
//      }
//    });
//  }

  public Future<Boolean> consume(String userId, String queueName) {
    Promise<Boolean> promise = Promise.promise();
    client
      .start()
      .onSuccess(
        handler -> {
          client.basicConsumer(
            queueName, new QueueOptions().setAutoAck(true),
            resultHandler -> {
              if (resultHandler.succeeded()) {
                System.out.println("The consumer is created! : ");
                System.out.println("Consumer Tag : " + resultHandler.result().consumerTag());
                System.out.println("Queue Name : " + resultHandler.result().queueName());
                RabbitMQConsumer rmqConsumer = resultHandler.result();
                rmqConsumer.handler(
                  rabbitMQMessage -> {
                    System.out.println("Message received in consumer : " + rabbitMQMessage.body().toString());
                    this.updateSize(rabbitMQMessage, userId, queueName);
                    promise.tryComplete(true);
                  });
              } else {
                System.out.println(
                  "Failure while receiving the message "
                    + resultHandler.cause().getMessage());
                resultHandler.cause().printStackTrace();
                promise.tryComplete(false);
              }
            });
        });
    return promise.future();
  }

  // queue1 consumed 12
  // queue2 consumed 13
  // queue3 consumed 14
  // queue4 consumed 15
  // total consumption = 12 + 13 + 14 + 15
  public Future<JsonObject> statusForUser(String userId) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("totalDataConsumed", monitor.getTotalUserData(userId));
    return Future.succeededFuture(jsonObject);
  }

  public void updateSize(RabbitMQMessage message, String userId, String queueName) {
    System.out.println("hereee0000");
    long size = InstrumentationAgent.getObjectSize(message);
    System.out.println("heree111");
    System.out.println("object size : " + size);
    List<QueueSize> list = monitor.getUserData(userId);
    if (list == null) {
      monitor.addUserData(userId, queueName, size);
      return;
    }
    for (QueueSize entry : list) {
      long currentSum = entry.getQueueSize();
      String entryQueueName = entry.getQueueName();
      if (entryQueueName.equals(queueName)) {
        long cumulativeSum = currentSum + size;
        entry.setQueueSize(cumulativeSum);
      }
    }
  }
}
