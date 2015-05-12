package io.autoscaling.devopscon;

import io.autoscaling.devopscon.cache.RedisReadVerticle;
import io.autoscaling.devopscon.properties.ServerProperties;
import io.autoscaling.devopscon.util.CloudUtil;
import io.autoscaling.devopscon.util.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Created by sascha.moellering on 11/05/2015.
 */
public class StarterVerticle extends Verticle {
    private static final Logger LOGGER = LogManager.getLogger(StarterVerticle.class);

    public void start() {

        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

        LOGGER.info("Deploying Verticles: ");

        LOGGER.info("Deploying " + HttpRequestProcessorVerticle.class.getName());
        container.deployVerticle(HttpRequestProcessorVerticle.class.getName());

        LOGGER.info("Deploying " + RedisReadVerticle.class.getName());
        container.deployVerticle(RedisReadVerticle.class.getName());

        Environment environment = CloudUtil.getInstance().getEnvironment();

        if (environment == Environment.AZURE) {
            LOGGER.info("Deploying " + Constants.MODULE_SERVICE_BUS);
            container.deployModule(Constants.MODULE_SERVICE_BUS, getServiceBusConfig());
        } else if (environment == Environment.AWS) {
            LOGGER.info("Deploying " + Constants.MODULE_KINESIS);
            container.deployModule(Constants.MODULE_KINESIS, getKinesisModuleConfig());
        } else {
            LOGGER.info("Deploying " + Constants.MODULE_KAFKA);
            container.deployModule(Constants.MODULE_KAFKA, getKafkaModuleConfig(ServerProperties.getInstance()));
        }
    }

    /**
     * Returns the configuration for "mod-service-bus" module deployment.
     *
     * @return
     */
    private JsonObject getServiceBusConfig() {

        JsonObject config = new JsonObject();
        config.putString("address", Constants.MESSAGE_BUS);
        config.putString("provider.url", "file:///tmp/servicebus.properties");

        return config;
    }

    /**
     * Returns the configuration for "mod-kafka" module deployment.
     *
     * @return configuration for mod-kafka module
     */
    JsonObject getKafkaModuleConfig(ServerProperties serverProperties) {
        final JsonObject config = new JsonObject();
        config.putString("address", Constants.MESSAGE_BUS);
        config.putString("metadata.broker.list", serverProperties.getKafkaBrokerList()); // KafkaConstants.BROKER_LIST);
        config.putString("kafka-topic", serverProperties.getKafkaTopic());               // KafkaConstants.TOPIC);
        config.putString("kafka-partition", serverProperties.getKafkaPartition());       // KafkaConstants.PARTITION);
        config.putNumber("request.required.acks", Constants.REQUEST_ACKS);
        config.putString("serializer.class", Constants.BYTE_SERIALIZER_CLASS);

        return config;
    }

    /**
     * Returns the configuration for "mod-kinesis" module deployment.
     *
     * @return configuration for mod-kinesis module
     */
    JsonObject getKinesisModuleConfig() {
        JsonObject config = new JsonObject();
        config.putString("address", Constants.MESSAGE_BUS);
        config.putString("streamName", Constants.KINESIS_STREAM);
        config.putString("partitionKey", Constants.KINESIS_PARTITION_KEY);
        config.putString("region", CloudUtil.getInstance().getRegion());

        return config;
    }
}
