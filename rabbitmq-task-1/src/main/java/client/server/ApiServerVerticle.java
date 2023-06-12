package client.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static client.server.Utils.*;

public class ApiServerVerticle extends AbstractVerticle {
  private RabbitClient rabbitClient;

  @Override
  public void start() throws IOException, TimeoutException {

    Router router = Router.router(vertx);
    rabbitClient = new RabbitClient(vertx);

//    Future result = rabbitClient.eventExchange();
//    result.onSuccess(handler -> {
//      System.out.println("Success");
//    });
//    result.onFailure(handler -> {
//      System.out.println("Failure");
//    });
    EventListener listener = new EventListener();
    listener.listen();
    router.get(API_MONITOR)
      .handler(this::monitor);
    router.get(API_STATUS)
      .handler(this::status);
    vertx.createHttpServer().requestHandler(router).listen(8080);


  } //m.userId.appName

  // /monitor
  // body:
  // userID :  abcd
  // queueName : m.abcd.app1
// this queueName naming convention could be changed


  private void monitor(RoutingContext routingContext) {
//    JsonObject body = routingContext.body().asJsonObject();
//    System.out.println("routingContext " + routingContext.body());
    System.out.println("query param : " + routingContext.request().getParam(PAYLOAD_QUEUE_NAME));
    System.out.println("query param : " + routingContext.request().getParam(PAYLOAD_USERID));
    String userId = routingContext.request().getParam(PAYLOAD_USERID);
    String queueName = routingContext.request().getParam(PAYLOAD_QUEUE_NAME);
    HttpServerResponse response = routingContext.response();

    Future<Boolean> createConsumer = rabbitClient.consume(userId, queueName);
    createConsumer
      .onFailure(handler -> {
        System.out.println("Oh oh something went wrong : " + handler.getCause().getMessage());
        handler.getCause().printStackTrace();
      })
      .onComplete(handler -> {
      if (handler.succeeded()) {
        System.out.println("Consumer is created for the given queue!  ");
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
    MultiMap params = routingContext.request().params();

    String userId = params.get(PAYLOAD_USERID);
    Future<JsonObject> getStatus = rabbitClient.statusForUser(userId);
    getStatus.onComplete(handler -> {
      if (getStatus.succeeded() && getStatus.result() != null) {
        handleResponse(200, getStatus.result().encode(), response);
      } else {
        handleResponse(500, getStatus.cause().getMessage(), response);
      }
    });
  }


}
