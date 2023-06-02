package client.server;


import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

public class RabbitClient {
  private RabbitMQClient rabbitMQClient;
  private RabbitMQOptions rabbitMQOptions;

  public Future<Boolean> create(String userId, String queueName) {

    return null;
  }

  public Future<JsonObject> status() {
    return null;
  }


}
