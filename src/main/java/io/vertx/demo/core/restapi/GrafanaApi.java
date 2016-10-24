package io.vertx.demo.core.restapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;


import io.vertx.demo.util.Runner;


/**
 * Created by jibou on 24/10/16.
 * This exposes an API to use the Grafana simple-Json plug-in
 * See https://github.com/grafana/simple-json-datasource
 *
 */
public class GrafanaApi extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) { Runner.runClusteredExample(GrafanaApi.class); }

    @Override
    public void start(Future<Void> fut) throws Exception {

        //Web router object
        Router router = Router.router(vertx);

        // Bus settings and instance
        final String busAddress = "median_temperature";
        final String busDataRequest = "data_request";
        EventBus eb = vertx.eventBus();


        // Bind "/" to a 200 OK response.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .setStatusCode(200)
                    .end("");
            System.out.println("got hit");
        });


        // Bind "/search" to
        router.route("/search").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            eb.send(busDataRequest, new JsonObject().put("median", true), res -> {
                response.putHeader("content-type", "text/html")
                        .setStatusCode(200)
                        .end(res.result().body().toString());
            });

        });



        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );


    }

}
