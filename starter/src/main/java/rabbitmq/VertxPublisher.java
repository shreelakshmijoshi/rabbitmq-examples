package rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQPublisher;

import java.util.HashMap;
import java.util.Map;


public class VertxPublisher {
  private static final String MESSAGE_TO_BE_SENT = "This is a message from the vert.x rabbitmq client producer";
  private static final String DEFAULT_EXCHANGE = "my-exchange";
  public static final String DEFAULT_QUEUE = "my-queue";
  private static final String DEFAULT_EXCHANGE_TYPE = "fanout";
  private RabbitMQClient client;
  public VertxPublisher(RabbitMQClient rabbitMQClient)
  {
    this.client = rabbitMQClient;
  }

  /**
   * Will create an exchange if not already declared. Will keep the exchange as it is if already declared
   */
  public void createExchange() {
    // also since the exchange is fanout we wouldn't require binding key for now
    // tho it does require binding of the exchange with the queue
    this.client.exchangeDeclare(
        DEFAULT_EXCHANGE,
        DEFAULT_EXCHANGE_TYPE,
        true,
        false,
        handler -> {
          if (handler.succeeded()) {
            System.out.println("Exchange successfully declared ");
          } else {
            System.out.println(
                "Oh oh, something went wrong while declaring exchange : "
                    + handler.cause().getMessage());
          }
        });
  }


  /**
   * Will create an exchange if not already declared. Will keep the exchange as it is if already declared
   * Additional configs can be added while declaring the queue
   */
  public void createQueue()
  {
    this.client.queueDeclare(
        DEFAULT_QUEUE,
        true,
        false,
        false,
        handler -> {
          if (handler.succeeded()) {
            System.out.println("Queue declared successfully");
          } else {
            System.out.println("Something went wrong while declaring queue : "+ handler.cause().getMessage());
            handler.cause().printStackTrace();
          }
        });
  }

  /**
   * Queue Binding here is happening by default and the message is sent
   * as direct exchange
   * here the queue name = routing key
   */
  public void publishMessageToQueueWithoutExchange()
  {
    Buffer bufferMessage = Buffer.buffer(MESSAGE_TO_BE_SENT);
      client.basicPublish("",DEFAULT_QUEUE, bufferMessage, handler -> {
        if (handler.succeeded())
        {
          System.out.println("Message sent to the queue directly !");
        }
        else
        {
          System.out.println("There was a problem while sending the message to the queue : " + handler.cause().getMessage());
          handler.cause().printStackTrace();
        }
      });
  }

  /**
   * It publishes the message to the queue directly and blocks
   * the channel until a confirmation is recieved from the channel that the message is
   * received in the server
   * Also publishing without exchange
   */
  public void publishMsg2QueueWithChannelConfirm()
  {
    // put the channel in confirm mode
    this.client.confirmSelect(
        confirmHandler -> {
          if (confirmHandler.succeeded()) {
            Buffer buffer = Buffer.buffer(MESSAGE_TO_BE_SENT);
            this.client.basicPublish(
                "",
                DEFAULT_QUEUE,
                buffer,
                handler -> {
                  if (handler.succeeded()) {
                    System.out.println(
                        "Message is published to the queue and the channel is in confirm mode");
                  } else {
                    System.out.println(
                        "Channel in confirm mode and message is not published : "
                            + handler.cause().getMessage());
                    handler.cause().printStackTrace();
                  }
                });
          } else {
            System.out.println("channel failed to confirm : " + confirmHandler.cause().getMessage());
            confirmHandler.cause().printStackTrace();
          }
        });
  }


  /**
   * Here the confirmation from the channel that the message is received,
   * confirmation happens asynchronously which achieves greater throughput (amount of work done in unit time)
   * can confirm multiple messages in one go
   * but when the confirmation is sent back to the user / publisher it is one acknowledgement at a time
   * Also publishing without exchange
   *
   *
   * Since the asynchronous confirmations can confirm multiple messages in one go,
   * it is important for the client to track all the messages in order that they were published
   * so it would be convenient to resend the messages if they are not confirmed
   *
   */
  public void reliableMessagePublishing(Vertx vertx)
  {
    RabbitMQPublisher publisher = RabbitMQPublisher.create(vertx,this.client,null);
// adding all the messages that need to be published in an internal queue
// so that it is convenient to resend them if they are not confirmed
// the messages are removed from the queue once they are confirmed
    Map<String,String> messages = new HashMap<>();
    messages.put("0", "Zero");
    messages.put("1","One");
    messages.put("2","Two");

    messages.forEach((key,value) -> {

      BasicProperties basicProperties = new AMQP.BasicProperties.Builder().messageId(key).build();

      publisher.publish("",DEFAULT_QUEUE,basicProperties,Buffer.buffer(value));
    });

    publisher
        .getConfirmationStream()
        .handler(
            confirmationHandler -> {
              if (confirmationHandler.isSucceeded()) {
                System.out.println("Confirmed!");
                messages.remove(confirmationHandler.getMessageId());
              }
            });
  }

  /**
   * Sending the message from Publisher to Queue or exchange through RMQ needs something called deliveryTag
   * deliveryTag is a unique ID for each message
   * Now the RMQ wants the publisher to know its deliveryTag before the message is actually sent
   * And the RMQ sends out a bunch of different delivery tags to the producer as sends a bunch of messages further
   * This puts the publisher in the dilemma of identify the bunch of deliveryTags
   * So, RabbitMQClient keeps track of delivery tags and it communicates with publisher before the actual message is sent
   * @param vertx
   */

  public void publishWithDeliveryTag(Vertx vertx)
  {
    this.client.basicPublishWithDeliveryTag(
        "",
        DEFAULT_QUEUE,
        null,
        Buffer.buffer(MESSAGE_TO_BE_SENT + " saying Hello :)"),
        deliveryTagHandler   -> {
          long deliveryTag = deliveryTagHandler;
//          System.out.println("Delivery Tag is : " + deliveryTag);
        },
        publishHandler -> {
          if (publishHandler.succeeded()) {
            System.out.println("Publishing message to Queue succeed! ");
          } else {
            System.out.println(
                "Publishing message to queue failed : " + publishHandler.cause().getMessage());
            publishHandler.cause().printStackTrace();
          }
        });
  }





}
