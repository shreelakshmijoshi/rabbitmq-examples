package client.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;


import static client.server.Utils.*;

public class ApiServerVerticle extends AbstractVerticle {
  private RabbitClient rabbitClient;

  @Override
  public void start() {

    Router router = Router.router(vertx);
    rabbitClient = new RabbitClient();
    router.post(API_MONITOR)
      .handler(this::monitor);
    router.get(API_STATUS)
      .handler(this::status);
    vertx.createHttpServer().requestHandler(router).listen(8080);

  }

  private void monitor(RoutingContext routingContext) {
    JsonObject body = routingContext.body().asJsonObject();
    String userId = body.getString(PAYLOAD_USERID);
    String queueName = body.getString(PAYLOAD_QUEUE_NAME);
    HttpServerResponse response = routingContext.response();

    Future<Boolean> createConsumer = rabbitClient.create(userId, queueName);
    createConsumer.onComplete(handler -> {
      if (handler.succeeded()) {
        System.out.println("Consumer is created for the given queue!");
        handleResponse(201, "Consumer is created for the given queue!", response);
      } else {
        System.out.println("Something went wrong while creating the consumer : " + handler.cause().getMessage());
        handleResponse(500, "Something went wrong while creating the consumer", response);
      }
    });
  }

  public void handleResponse(int statusCode, String message, HttpServerResponse response) {
    response.putHeader("content-type", "application/json").setStatusCode(statusCode).end(message);
  }

  public void status(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();

    Future<JsonObject> getStatus = rabbitClient.status();
    getStatus.onComplete(handler -> {
      if (getStatus.succeeded() && getStatus.result() != null) {
        handleResponse(200, getStatus.result().encode(), response);
      } else {
        handleResponse(500, getStatus.cause().getMessage(), response);
      }
    });
  }


}
