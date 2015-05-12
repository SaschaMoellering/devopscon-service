package io.autoscaling.devopscon;

import io.autoscaling.devopscon.util.VerticleNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.UUID;

/**
 * Created by sascha.moellering on 09/05/2015.
 */
public class HttpRequestProcessorVerticle extends Verticle {

    private static final Logger LOGGER = LogManager.getLogger(StarterVerticle.class);

    private EventBus eventBus;
    private HttpServer httpServer;

    @Override
    public void start() {
        eventBus = vertx.eventBus();

        RouteMatcher routeMatcher = new RouteMatcher();

        routeMatcher.get("/devopscon/:payload", req -> {
            JsonObject jsonObject = new JsonObject();
            eventBus.send(Constants.MESSAGE_BUS, jsonObject, (Message<Object> objectMessage) -> {
                req.response().end("Payload: " + req.params().get("payload"));
            });
        });

        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(routeMatcher).listen(8080);
    }

    @Override
    public void stop() {
        httpServer.close();
    }
}
