package io.autoscaling.devopscon.cache;


import io.autoscaling.devopscon.properties.ServerProperties;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;
import redis.clients.jedis.Jedis;


/**
 * This verticle is supposed to read the data out of the redis.
 * If there is none it makes an API call to the redis updater.
 */
public class RedisReadVerticle extends Verticle {
    private static final String REDIS_SERVER_HOST_SLAVE = ServerProperties.getInstance().getRedisServerHostSlave();
    private static final Integer REDIS_SERVER_PORT = ServerProperties.getInstance().getRedisServerPort();

    private EventBus eventBus;
    private Jedis jedis;

    private HttpClient client = HttpClientBuilder.create().build();

    /**
     * In this start method we set up the jedis connection.
     */
    public void start() {
        eventBus = vertx.eventBus();

        jedis = new Jedis(REDIS_SERVER_HOST_SLAVE, REDIS_SERVER_PORT);
        jedis.connect();
        eventBus.registerHandler(RedisReadVerticle.class.getSimpleName(), new TrackingDataHandler());
    }

    @Override
    public void stop() {
        jedis.disconnect();

    }

    private class TrackingDataHandler implements Handler<Message<JsonObject>> {
        @Override
        public void handle(Message<JsonObject> event) {
            if (!jedis.isConnected()) {
                jedis.connect();
            }
            readDataFromRedis(event.body());
        }
    }

    private void readDataFromRedis(JsonObject trackingDataObject) {
        /*
        JsonObject storedCacheValues = trackingDataObject.getObject(TrackingConstants.TRACKING_CACHE_KEYS_VALUES);

        JsonArray keysJson = trackingDataObject.getField(TrackingConstants.TRACKING_KEYS);
        List<String> keys = keysJson.toList();

        String value;

        for (String redisKey : keys) {

            value = jedis.get(redisKey);
            trackingDataObject.putObject(redisKey, storedCacheValues);
        }

        trackingDataObject.putNumber(TrackingConstants.TRACKING_LRU_CACHE_WRITE, 1);
        eventBus.send(VerticleNames.LRU_CACHE_VERTICLE_NAME, trackingDataObject);
        */
    }
}
