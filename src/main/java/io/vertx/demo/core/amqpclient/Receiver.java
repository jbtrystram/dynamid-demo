package io.vertx.demo.core.amqpclient;

import io.vertx.amqpbridge.AmqpBridge;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.demo.util.Runner;
import io.vertx.amqpbridge.AmqpConstants;
import io.vertx.core.eventbus.MessageConsumer;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Receiver extends AbstractVerticle {
  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runExample(Receiver.class);
  }

  // Settings
  final String amqptopicAddress = "temperature";
  final String amqpServer = "localhost";

  @Override
  public void start() throws Exception {
    AmqpBridge bridge = AmqpBridge.create(vertx);


    // Start the bridge, then use the event loop thread to process things thereafter.
    bridge.start(amqpServer, 5672, res -> {
      if(!res.succeeded()) {
        System.out.println("Bridge startup failed: " + res.cause());
        return;
      }

      // Start the eventBus
        final EventBus eb = vertx.eventBus();
        final String busAddress = "raw_temperature";


      // Set up a consumer using the bridge, register a handler for it.
      MessageConsumer<JsonObject> consumer = bridge.createConsumer(amqptopicAddress);
      consumer.handler(vertxMsg -> {
        JsonObject amqpMsgPayload = vertxMsg.body();
        Object amqpBody = amqpMsgPayload.getValue(AmqpConstants.BODY);

        // Remove quotes around id
        JsonObject message = new JsonObject (amqpBody.toString());
        message.put("id", message.getString("id").substring(1,7));
        System.out.println(message.encode());
        eb.publish(busAddress, message);
      });
    });

  }
}
