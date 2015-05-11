package com.sm.devopscon.properties;

import com.sm.devopscon.util.CloudUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Responsible for loading and initializing Redis server related properties into the application from the
 * provided server_staging.properties file.
 *
 * @author Mariam Hakobyan
 */
public final class ServerProperties extends PropertyResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerProperties.class);

    private static final String KAFKA_BROKER_LIST = "kafka.broker.list";
    private static final String KAFKA_TOPIC = "kafka.topic";
    private static final String KAFKA_PARTITION = "kafka.partition";

    private static final String REDIS_SERVER_HOST_MASTER = "redis.server.host.master";
    private static final String REDIS_SERVER_HOST_SLAVE = "redis.server.host.slave";
    private static final String REDIS_SERVER_PORT = "redis.server.port";

    private static final String ELASTICACHE_SERVER_HOST_MASTER = "elasticache.server.host.master";
    private static final String ELASTICACHE_SERVER_HOST_SLAVE = "elasticache.server.host.slave";
    private static final String ELASTICACHE_SERVER_PORT = "elasticache.server.port";

    private static final String REDIS_UPDATER_ADDRESS = "redis.updater.address";

    private static final String MONITORING_PORT = "monitoring.port";
    private static final String HTTP_SERVER_PORT = "http.server.port";
    private static final String TCP_ACCEPT_QUEUE = "tcp.accept.queue";

    private static final String BACKUP_FILE_PATH = "backup.file.path";
    private static ServerProperties instance = null;
    private String instanceId;
    private int monitoringPort = 80;   //standard value
    private int httpServerPort = 8080; //standard value
    private int tcpAcceptQueue = 1000; //standard value
    private String redisServerHostMaster;
    private String redisServerHostSlave;
    private Integer redisServerPort;
    private String kafkaBrokerList;
    private String kafkaTopic;
    private String kafkaPartition;
    private String redisUpdaterAddress;
    private boolean isWorkingPathEnabled = false;
    private String backupFilePath;
    private boolean useIpResolution = true;     // enable geo-ip resolution: country, subdivision and city
    private int ipTruncationLevel = 0;          // number of ip octets do be truncated; default value: no octets

    private int kafkaexportUnsentEventListSize = 1000;   // failed send event-list capacity (before backup saving)

    /**
     * Initializes Redis Server related properties provided by server_staging.properties file into the application.
     */
    private ServerProperties() {

        String stage = System.getProperty("stage");

        if (stage == null) {
            stage = System.getenv("stage");
        }

        LOGGER.info("Stage Environment: " + stage);

        if (stage == null || stage.trim().length() == 0) {
            LOGGER.info("NO ENVIRONMENT STAGE HAS BEEN SET, SWITCHING TO LOCAL!");
            stage = "local";
        }

        if (stage.equalsIgnoreCase("staging")) {
            isWorkingPathEnabled = true;
        }

        // Load a properties file
        Properties prop = getPropertiesFromClasspath("server_" + stage + ".properties");

        if (prop != null) {

            if (CloudUtil.getInstance().isEnvironmentAWS()) {
                redisServerHostMaster = prop.getProperty(ELASTICACHE_SERVER_HOST_MASTER);
                redisServerHostSlave = prop.getProperty(ELASTICACHE_SERVER_HOST_SLAVE);
                redisServerPort = Integer.parseInt(prop.getProperty(ELASTICACHE_SERVER_PORT));
                instanceId = CloudUtil.getInstance().retrieveInstanceId();
            } else {
                redisServerHostMaster = prop.getProperty(REDIS_SERVER_HOST_MASTER);
                redisServerHostSlave = prop.getProperty(REDIS_SERVER_HOST_SLAVE);
                redisServerPort = Integer.parseInt(prop.getProperty(REDIS_SERVER_PORT));
                instanceId = CloudUtil.getInstance().createDcInstanceId();
            }
            LOGGER.info("Instance_ID = " + instanceId);
            httpServerPort = Integer.parseInt(prop.getProperty(HTTP_SERVER_PORT));
            monitoringPort = Integer.parseInt(prop.getProperty(MONITORING_PORT));
            tcpAcceptQueue = Integer.parseInt(prop.getProperty(TCP_ACCEPT_QUEUE));
            redisUpdaterAddress = prop.getProperty(REDIS_UPDATER_ADDRESS);

            kafkaBrokerList = prop.getProperty(KAFKA_BROKER_LIST);
            kafkaTopic = prop.getProperty(KAFKA_TOPIC);
            kafkaPartition = prop.getProperty(KAFKA_PARTITION);

            backupFilePath = prop.getProperty(BACKUP_FILE_PATH);
        }
    }

    /**
     * Creates a synchronized singleton instance of ServerProperties.
     */
    public static ServerProperties getInstance() {
        if (instance == null) {
            synchronized (ServerProperties.class) {
                if (instance == null) {
                    instance = new ServerProperties();
                }
            }
        }
        return instance;
    }

    /**
     * The InstanceID of the current server.
     *
     * @return instanceID as String
     */
    public String getInstanceId() {
        return instanceId;
    }

    public boolean isWorkingPathEnabled() {
        return isWorkingPathEnabled;
    }

    public String getRedisServerHostMaster() {
        return redisServerHostMaster;
    }

    public String getRedisServerHostSlave() {
        return redisServerHostSlave;
    }

    public Integer getRedisServerPort() {
        return redisServerPort;
    }

    public int getMonitoringPort() {
        return monitoringPort;
    }

    public int getHttpServerPort() {
        return httpServerPort;
    }

    public int getTcpAcceptQueue() {
        return tcpAcceptQueue;
    }

    public String getKafkaPartition() {
        return kafkaPartition;
    }

    public String getKafkaBrokerList() {
        return kafkaBrokerList;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public String getBackupFilePath() {
        return backupFilePath;
    }

    public String getRedisUpdaterAddress() {
        return redisUpdaterAddress;
    }

    public int getIpTruncationLevel() {
        return ipTruncationLevel;
    }

    public boolean isIpResolutionEnabled() {
        return useIpResolution;
    }

    public int getKafkaexportUnsentEventListSize() {
        return kafkaexportUnsentEventListSize;
    }
}
