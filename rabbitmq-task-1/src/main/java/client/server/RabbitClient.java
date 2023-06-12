package client.server;


import com.rabbitmq.client.*;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static client.server.Utils.MESSAGE_SIZE_HEADER;

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
  }


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
                    Map<String, Object> headerMap = rabbitMQMessage.properties().getHeaders();
                    Long size = Long.valueOf(headerMap.get(MESSAGE_SIZE_HEADER).toString());
                    monitor.updateSize(rabbitMQMessage, userId, queueName, size);
                  });
                promise.complete(true);
              } else {
                System.out.println(
                  "Failure while receiving the message "
                    + resultHandler.cause().getMessage());
                resultHandler.cause().printStackTrace();
                promise.fail(resultHandler.cause().getMessage());
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

}
