package com.sm.devopscon;

/**
 * Created by sascha.moellering on 11/05/2015.
 */
public class Constants {

    public static final String URL_SEPARATOR = "/";

    public static final int REQUEST_ACKS = 0;
    public static final String STRING_SERIALIZER_CLASS = "kafka.serializer.StringEncoder";
    public static final String BYTE_SERIALIZER_CLASS = "kafka.serializer.DefaultEncoder";

    public static final String PAYLOAD = "payload";

    public static final String KINESIS_STREAM = "kinesisTrackingStream";
    public static final String KINESIS_PARTITION_KEY = "partitionkey";
    public static final String MOD_SERVICE_BUS_ADDRESS = "service.bus.azure";
    public static final String MODULE_SERVICE_BUS = "com.sm.vertx~mod-service-bus~1.0.0";
    public static final String TRACKING_BUS = "tracking.bus";

    public static final String REDIS_LOOKUP = "redis.trackingcode";
    public static final String REDIS_ADDRESS = "com.zanox.vertx_redis";
    public static final String SQS_QUEUE = "sqs.queue";


    public static final String TRACKING_REQUEST_PPV = "ppv"; // @ TODO: Needs to be deleted.
    public static final String TRACKING_SAVE_PREFIX = "kafka_";

    public static final String MODULE_KAFKA = "com.zanox.vertx~mod-kafka~1.1.1";
    public static final String MODULE_REDIS = "io.vertx~mod-redis~1.1.3";
    public static final String MODULE_KINESIS = "com.zanox.vertx.mods~mod-kinesis~1.4.13";
    public static final String BACKUP_EVENTS = "backupEvents";

    public static final String HTTP_SERVER_REQUEST_MAP = "HttpServerRequestMap";

    /* Non-instantiable class */
    private Constants() {
    }

}
