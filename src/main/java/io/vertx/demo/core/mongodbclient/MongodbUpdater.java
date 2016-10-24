package io.vertx.demo.core.mongodbclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;


import io.vertx.demo.util.Runner;

/**
 * Created by jibou on 24/10/16.
 */
public class MongodbUpdater extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Runner.runClusteredExample(MongodbUpdater.class);
    }

    @Override
    public void start() throws Exception {

        // Bus settings and instance
        final String busAddress = "raw_temperature";
        EventBus eb = vertx.eventBus();

        //mongoDB settings, get from config json file
        JsonObject mongoConfig = config();
        MongoClient mongoClient = MongoClient.createShared(vertx, mongoConfig);

        //When new message on bus" ID " : " (String) ",
        eb.<JsonObject> consumer(busAddress, message -> {
            mongoClient.insert(busAddress, message.body(), res -> {
                System.out.println("Inserted id: " + res.result());
            });
        });

        System.out.println("Ready!");
    }
}