package io.vertx.demo.core.restapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.impl.codecs.StringMessageCodec;
import io.vertx.core.json.JsonArray;
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

    private JsonArray formResponse(String incoming){
        JsonArray res = new JsonArray();
        JsonObject data = new JsonObject(incoming);

        res.add(new JsonObject()
                .put("target", "median_temp")
                .put("datapoints", new JsonArray()
                        .add(new JsonArray()
                                .add(data.getDouble("value"))
                                .add(data.getInteger("timestamp"))
                        )
                )
        );
        return res;
    }

    @Override
    public void start(Future<Void> fut) throws Exception {

        //Web router object
        Router router = Router.router(vertx);

        // Bus settings and instance
        final String busAddress = "median_temperature";
        final String busDataRequest = "data_request";
        EventBus eb = vertx.eventBus();


        // Enable reading requests' bodies
        router.route().handler(BodyHandler.create());

        // Bind "/" to a 200 OK response.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .setStatusCode(200)
                    .end("");
            System.out.println(" request recevied on / ");
        });


        // Bind "/search" to
        router.route("/search").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            System.out.println(" /search request");
                response.putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(200)
                        .end("[\"raw_temp\",\"median_temp\"]");
        });


        // Bind "/query" to
        router.route("/query").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();

            // request median value
            if (routingContext.getBodyAsJson().getJsonArray("targets")
                    .getJsonObject(0).getString("target").equals("median_temp")) {

                eb.send(busDataRequest, new JsonObject().put("median", true), res -> {
                    response.putHeader("content-type", "application/json; charset=utf-8")
                            .setStatusCode(200)
                            .end(formResponse(res.result().body().toString()).encode());
                });
            }
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
