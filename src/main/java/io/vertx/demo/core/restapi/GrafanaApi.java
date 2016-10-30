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

    // A JsonArray to store the median datapoints between requests (grafana doesn't do it).
    private JsonArray median = new JsonArray().add(new JsonObject()
            .put("target", "median_temp")
            .put("datapoints", new JsonArray()
            ));

    // Same : an array for each node
    private JsonArray node01 = new JsonArray().add(new JsonObject()
            .put("target", "node01")
            .put("datapoints", new JsonArray()
            ));

    // Same : an array for each node
    private JsonArray node02 = new JsonArray().add(new JsonObject()
            .put("target", "node02")
            .put("datapoints", new JsonArray()
            ));

    // Same : an array for each node
    private JsonArray node03 = new JsonArray().add(new JsonObject()
            .put("target", "node03")
            .put("datapoints", new JsonArray()
            ));

    // Same : an array for each node
    private JsonArray node04 = new JsonArray().add(new JsonObject()
            .put("target", "node04")
            .put("datapoints", new JsonArray()
            ));


    private JsonArray formResponse(String incoming, JsonArray array){

        JsonObject data = new JsonObject(incoming);

        array.getJsonObject(0).getJsonArray("datapoints")
                .add( new JsonArray()
                      .add(data.getDouble("value"))
                      .add(data.getLong("timestamp"))
                );

        return array;
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
                        .end(new JsonArray().add("node01")
                                            .add("node02")
                                            .add("node03")
                                            .add("node04")
                                            .add("median_temp").encode());
        });


        // Bind "/query" to
        router.route("/query").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            System.out.println("/query hit");

            // get requested value
            String requested = routingContext.getBodyAsJson().getJsonArray("targets")
                    .getJsonObject(0).getString("target");


            if (requested.equals("median_temp")) {
                eb.send(busDataRequest, new JsonObject().put("median", true), res -> {
                    System.out.println("median_temp requested");
                    response.putHeader("content-type", "application/json; charset=utf-8")
                            .setStatusCode(200)
                            .end(formResponse(res.result().body().toString(), median).encode());
                });
            }
            else if (requested.equals("node01")) {
                System.out.println("node01 requested");
                eb.send(busDataRequest, new JsonObject().put("requested", requested), res -> {
                    response.putHeader("content-type", "application/json; charset=utf-8")
                            .setStatusCode(200)
                            .end(formResponse(res.result().body().toString(), node01).encode());
                });
            }
            else if (requested.equals("node02")) {
                eb.send(busDataRequest, new JsonObject().put("requested", requested), res -> {
                    response.putHeader("content-type", "application/json; charset=utf-8")
                            .setStatusCode(200)
                            .end(formResponse(res.result().body().toString(), node02).encode());
                });
            }
            else if (requested.equals("node03")) {
                eb.send(busDataRequest, new JsonObject().put("requested", requested), res -> {
                    response.putHeader("content-type", "application/json; charset=utf-8")
                            .setStatusCode(200)
                            .end(formResponse(res.result().body().toString(), node03).encode());
                });
            }
            else if (requested.equals("node04")) {
                eb.send(busDataRequest, new JsonObject().put("requested", requested), res -> {
                    response.putHeader("content-type", "application/json; charset=utf-8")
                            .setStatusCode(200)
                            .end(formResponse(res.result().body().toString(), node04).encode());
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
