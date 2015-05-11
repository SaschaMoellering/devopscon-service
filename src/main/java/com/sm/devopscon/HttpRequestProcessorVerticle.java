package com.sm.devopscon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.UUID;

/**
 * Created by sascha.moellering on 09/05/2015.
 */
public class HttpRequestProcessorVerticle extends Verticle {

    private static final Logger LOGGER = LogManager.getLogger(StarterVerticle.class);

    private EventBus eventBus;

    @Override
    public void start() {
        eventBus = vertx.eventBus();

        vertx.createHttpServer().requestHandler(req -> {

            LOGGER.info("Sending request");

            UUID requestUuid = UUID.randomUUID();

            JsonObject trackingData = new JsonObject();
            trackingData.putString("uuid", requestUuid.toString());
            trackingData.putString("payload", "TestString");

            eventBus.send(Constants.MOD_SERVICE_BUS_ADDRESS, trackingData);

            req.response().setStatusCode(200).end();
        }).listen(8080);
    }
}
