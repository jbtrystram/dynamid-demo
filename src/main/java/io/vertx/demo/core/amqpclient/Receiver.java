package io.vertx.demo.core.amqpclient;

import io.vertx.amqpbridge.AmqpBridge;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.demo.util.Runner;
import io.vertx.amqpbridge.AmqpConstants;
import io.vertx.core.eventbus.MessageConsumer;

import io.vertx.rabbitmq.RabbitMQClient;


/*
 * @author <a href="http://tfox.org">Tim Fox</a>
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
    /*AmqpBridge bridge = AmqpBridge.create(vertx);


    // Start the bridge, then use the event loop thread to process things thereafter.
    bridge.start("localhost", 5672, res -> {
      if(!res.succeeded()) {
        System.out.println("Bridge startup failed: " + res.cause());
        return;
      }
      System.out.println("Connected to AMQP broker");
*/
      // Start the eventBus
        final EventBus eb = vertx.eventBus();
        final String busAddress = "raw_temperature";


    /*
      // Set up a consumer using the bridge, register a handler for it.
      MessageConsumer<JsonObject> consumer = bridge.createConsumer(amqptopicAddress);
      consumer.handler(vertxMsg -> {
        JsonObject amqpMsgPayload = vertxMsg.body();
        Object amqpBody = amqpMsgPayload.getValue(AmqpConstants.BODY);

        System.out.println("new AMQP message: " + amqpBody.toString());

        eb.publish(busAddress, amqpBody);
      });
    });
*/
    //// RABBIT MQ CODE

    //config
    JsonObject config = new JsonObject().put("host", "10.45.0.153").put("port", 5672);

    RabbitMQClient client = RabbitMQClient.create(vertx, config);
    client.start(res -> {
      if(client.isConnected()){
      System.out.println("Client connected");


      // Create the event bus handler which messages will be sent to
      eb.consumer(busAddress, msg -> {
        JsonObject json = (JsonObject) msg.body();
        System.out.println("Got message: " + json.getString("body"));
      });

      // Setup the link between rabbitmq consumer and event bus address
      client.basicConsume("temperature", busAddress, consumeResult -> {
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

