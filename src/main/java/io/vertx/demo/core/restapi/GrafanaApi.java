package io.vertx.demo.core.restapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.demo.util.Runner;

/**
 * Created by jibou on 24/10/16.
 */
public class GrafanaApi extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) { Runner.runClusteredExample(GrafanaApi.class); }

    @Override
    public void start() throws Exception {

        // Bus settings and instance
        final String busAddress = "median_temperature";
        EventBus eb = vertx.eventBus();


}

}
