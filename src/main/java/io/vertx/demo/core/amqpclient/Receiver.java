package io.vertx.demo.core.amqpclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.demo.util.Runner;


import io.vertx.rabbitmq.RabbitMQClient;

/*
 * @author Oscar Carillo
 * @author JB Trystram
 */
public class Receiver extends AbstractVerticle {
  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runClusteredExample(Receiver.class);
  }

  // Settings
  final String amqptopicAddress = "temperature";
  final String amqpServer = "localhost";

  @Override
  public void start() throws Exception {

      // Start the eventBus
        final EventBus eb = vertx.eventBus();
        final String busAddress = "raw_temperature";

    //rabbitMQ client config
    JsonObject config = new JsonObject().put("host", amqpServer).put("port", 5672);

    RabbitMQClient client = RabbitMQClient.create(vertx, config);
    client.start(res -> {
      if(client.isConnected()){
      System.out.println("Client connected");

      //Send the message to the event bus address we use to process data
      eb.consumer(amqptopicAddress, msg -> {
        JsonObject json = (JsonObject) msg.body();
        System.out.println("AMQP message: " + json.getString("body"));
        eb.publish(busAddress, new JsonObject(json.getString("body")));
      });

      // Setup the link between rabbitmq consumer and event bus address
      client.basicConsume(amqptopicAddress, amqptopicAddress, consumeResult -> {
        if (consumeResult.succeeded()) {
          System.out.println("RabbitMQ consumer created !");
        } else {
          consumeResult.cause().printStackTrace();
        }
      });

      }else{
        // We could not connect
        System.out.println(res.cause());
      }
    });
    System.out.println("Client rabbitmq done.");
  }
}

