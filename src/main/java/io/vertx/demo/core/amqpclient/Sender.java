package io.vertx.demo.core.amqpclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.demo.util.Runner;
import io.vertx.amqpbridge.AmqpBridge;
import io.vertx.amqpbridge.AmqpConstants;
import io.vertx.core.eventbus.MessageProducer;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Sender extends AbstractVerticle {

  // Settings
  final String amqptopicAddress = "temperature";
  final String amqpServer = "localhost";

  private int count = 1;

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runClusteredExample(Sender.class);
  }

    @Override
    public void start() throws Exception {
        AmqpBridge bridge = AmqpBridge.create(vertx);

        // Start the bridge, then use the event loop thread to process things thereafter.
        bridge.start(amqpServer, 5672, res -> {
            if(!res.succeeded()) {
                System.out.println("Bridge startup failed: " + res.cause());
                return;
            }

            // Set up a producer using the bridge, send a message with it.
            MessageProducer<JsonObject> producer = bridge.createProducer(amqptopicAddress);

            // Schedule sending of a message every second
            System.out.println("Producer created, scheduling sends.");
            vertx.setPeriodic(1000, v -> {
                JsonObject amqpMsgPayload = new JsonObject();
                amqpMsgPayload.put(AmqpConstants.BODY, "Hey, value is " + count);

                producer.send(amqpMsgPayload);

                System.out.println("Sent message: " + count++);
            });
        });
    }


}
