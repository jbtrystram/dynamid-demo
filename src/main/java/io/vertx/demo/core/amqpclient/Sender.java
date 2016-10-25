package io.vertx.demo.core.amqpclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.demo.util.Runner;
import io.vertx.amqpbridge.AmqpBridge;
import io.vertx.amqpbridge.AmqpConstants;
import io.vertx.core.eventbus.MessageProducer;

import java.util.Random;
import java.time.Instant;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Sender extends AbstractVerticle {
    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Runner.runExample(Sender.class);
    }


  // Settings
  final String amqptopicAddress = "temperature";
  final String amqpServer = "localhost";

    private double baseTemp = 19;
    private int count = 1;
    private double getTemp() {
        Random generator = new Random();
        return this.baseTemp + ((generator.nextDouble()*2)-1);
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
                amqpMsgPayload.put("timestamp", Instant.now().getEpochSecond())
                .put("id", 3).put("value",this.getTemp());

                producer.send(new JsonObject().put(AmqpConstants.BODY,  amqpMsgPayload));

            });
        });
    }


}
