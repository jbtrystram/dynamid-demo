package io.vertx.demo.core.amqpclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.demo.util.Runner;

/**
 * Created by jibou on 24/10/16.
 */
public class BusListener extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Runner.runClusteredExample(BusListener.class);
    }

    @Override
    public void start() throws Exception {

        // Bus settings
        final String busAddress = "raw_temperature";

        EventBus eb = vertx.eventBus();

        eb.consumer(busAddress, message -> System.out.println("Received data on eventBus: " + message.body()));

        System.out.println("Ready!");
    }
}