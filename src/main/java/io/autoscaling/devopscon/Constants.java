package io.autoscaling.devopscon;

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
    public static final String MESSAGE_BUS = "message.bus";

    public static final String MODULE_KAFKA = "com.zanox.vertx~mod-kafka~1.1.1";
    public static final String MODULE_KINESIS = "com.zanox.vertx.mods~mod-kinesis~1.4.13";

    /* Non-instantiable class */
    private Constants() {
    }

}
