package io.autoscaling.devopscon;

import io.autoscaling.devopscon.cache.RedisReadVerticle;
import io.autoscaling.devopscon.util.VerticleNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
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

            String payload = req.params().get("payload");
            JsonObject jsonObject = new JsonObject();

            JsonArray jsonArray = new JsonArray();
            jsonArray.add(payload);
            jsonObject.putArray(Constants.REDIS_KEYS, jsonArray);

            eventBus.send(RedisReadVerticle.class.getSimpleName(), jsonObject, (Message<JsonObject> objectMessage) -> {

                String value = objectMessage.body().getString(payload);

                JsonObject message = new JsonObject();
                message.putBinary("payload", (payload + value).getBytes());

                eventBus.send(Constants.MESSAGE_BUS, message, (Message<Object> _objectMessage) -> {
                    req.response().end("Payload: " + (payload + value));
                });
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
