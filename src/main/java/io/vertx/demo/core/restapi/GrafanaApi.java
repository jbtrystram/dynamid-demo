package io.vertx.demo.core.restapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
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

    // tracing what grafana asked.
    boolean queryMedium, query01, query02, query03, query04 = false;

    // A JsonArray to store the median datapoints between requests (grafana doesn't do it).
    private JsonObject median = new JsonObject()
            .put("target", "median_temp")
            .put("datapoints", new JsonArray()
            );

    // Same : an array for each node
    private JsonObject node01 = new JsonObject()
            .put("target", "node01")
            .put("datapoints", new JsonArray()
            );

    // Same : an array for each node
    private JsonObject node02 = new JsonObject()
            .put("target", "node02")
            .put("datapoints", new JsonArray()
            );

    // Same : an array for each node
    private JsonObject node03 = new JsonObject()
            .put("target", "node03")
            .put("datapoints", new JsonArray()
            );

    // Same : an array for each node
    private JsonObject node04 = new JsonObject()
            .put("target", "node04")
            .put("datapoints", new JsonArray()
            );


    private JsonObject formResponse(String incoming, JsonObject array){
        JsonObject data = new JsonObject(incoming);

        array.getJsonArray("datapoints")
                .add( new JsonArray()
                      .add(data.getDouble("value"))
                      .add(data.getLong("timestamp"))
                );
        return array;
    }

    // event Bus settings and instance
    final String busDataRequest = "data_request";

    public Future<JsonObject> getMedianTemp(EventBus eb) {
        Future<JsonObject> callAFuture = Future.future();

        eb.send(busDataRequest, new JsonObject().put("requested", "median_temp"), res -> {
                    JsonObject data = formResponse(res.result().body().toString(), median);
                    callAFuture.complete(data);
                });
        return callAFuture;
    }

    public Future<JsonObject> getNode01Temp(EventBus eb) {
        Future<JsonObject> callAFuture = Future.future();

        eb.send(busDataRequest, new JsonObject().put("requested", "node01"), res -> {
            JsonObject data = formResponse(res.result().body().toString(), node01);
            callAFuture.complete(data);
        });
        return callAFuture;
    }

    public Future<JsonObject> getNode02Temp(EventBus eb) {
        Future<JsonObject> callAFuture = Future.future();

        eb.send(busDataRequest, new JsonObject().put("requested", "node02"), res -> {
            JsonObject data = formResponse(res.result().body().toString(), node02);
            callAFuture.complete(data);
        });
        return callAFuture;
    }
    public Future<JsonObject> getNode03Temp(EventBus eb) {
        Future<JsonObject> callAFuture = Future.future();

        eb.send(busDataRequest, new JsonObject().put("requested", "node03"), res -> {
            JsonObject data = formResponse(res.result().body().toString(), node03);
            callAFuture.complete(data);
        });
        return callAFuture;
    }

    public Future<JsonObject> getNode04Temp(EventBus eb) {
        Future<JsonObject> callAFuture = Future.future();

        eb.send(busDataRequest, new JsonObject().put("requested", "node04"), res -> {
            JsonObject data = formResponse(res.result().body().toString(), node04);
            callAFuture.complete(data);
        });
        return callAFuture;
    }


    @Override
    public void start(Future<Void> fut) throws Exception {

        //Web router object
        Router router = Router.router(vertx);

        // Enable reading requests' bodies
        router.route().handler(BodyHandler.create());

        //event bus
        EventBus eb = vertx.eventBus();

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

            System.out.println("/query hit " );

            JsonArray dataResponse = new JsonArray();

            CompositeFuture.all(getMedianTemp(eb), getNode01Temp(eb), getNode02Temp(eb),
                    getNode03Temp(eb), getNode04Temp(eb)).setHandler(connections -> {
                // all the Futures completed
                dataResponse.add( (JsonObject) connections.result().resultAt(0));
                dataResponse.add( (JsonObject) connections.result().resultAt(1));
                dataResponse.add( (JsonObject) connections.result().resultAt(2));
                dataResponse.add( (JsonObject) connections.result().resultAt(3));
                dataResponse.add( (JsonObject) connections.result().resultAt(4));

                // Create response
                response.putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(200)
                        .end(dataResponse.encode());
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
