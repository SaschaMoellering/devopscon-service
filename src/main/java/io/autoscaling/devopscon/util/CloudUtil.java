package io.autoscaling.devopscon.util;


import com.microsoft.windowsazure.serviceruntime.RoleEnvironment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Sascha Möllering
 * <p/>
 * This Util class is responsible for all methods which are related to AWS and Azure.
 */
public final class CloudUtil {

    public static final String METADATA_URL = "http://169.254.169.254/latest/meta-data/placement/availability-zone";
    public static final String REGION = "region";
    public static final String DEFAULT_REGION = "eu-west-1";
    public static final String BUCKET_NAME = "viewtracker";
    private static final Logger LOGGER = LogManager.getLogger(CloudUtil.class);
    private static final int TIMEOUT = 50;
    private static final CloudUtil CLOUD_UTIL = new CloudUtil();
    private Boolean isAmazon, isAzure;
    private String instanceId = null;

    private CloudUtil() {
    }

    public static CloudUtil getInstance() {
        return CLOUD_UTIL;
    }

    /**
     * Returns the region the application is running in.
     *
     * @return The region the application is running in
     */
    public String getRegion() {
        try {
            if (isEnvironmentAWS()) {
                return this.getRegionFromMetadata();
            }
        } catch (IOException exc) {
            LOGGER.error(exc.toString(), exc);
        }

        return DEFAULT_REGION;
    }

    /**
     * Returns the bucket name the application is running in.
     *
     * @return The bucket name the application is running in
     */
    public String getBucketName() {
        try {
            if (isEnvironmentAWS()) {
                return BUCKET_NAME + "." + this.getRegionFromMetadata();
            }
        } catch (IOException exc) {
            LOGGER.error(exc.toString(), exc);
        }

        return BUCKET_NAME + "." + DEFAULT_REGION;
    }

    public Environment getEnvironment() {
        if (isEnvironmentAWS()) {
            return Environment.AWS;
        } else if (isEnvironmentAzure()) {
            return Environment.AZURE;
        } else {
            return Environment.DC;
        }
    }

    public boolean isEnvironmentAzure() {

        if (isAzure == null) {
            isAzure = true;

            try {
                RoleEnvironment.getRoles();
                boolean isEmulated = RoleEnvironment.isEmulated();

                if (isEmulated) {
                    LOGGER.info("Azure environment is emulated");
                    isAzure = false;
                } else {
                    LOGGER.info("Azure environment is not emulated");
                }
            } catch (Exception exc) {
                isAzure = false;
                LOGGER.info("Exception " + exc.getMessage() + " is thrown -> not in Azure");
            }
        }

        return isAzure;
    }

    /**
     * Checks if we are running in AWS.
     *
     * @return true if the app is running in AWS, otherwise false
     */
    public boolean isEnvironmentAWS() {
        if (isAmazon == null) {
            isAmazon = true;
            BufferedReader in = null;

            try {
                // Checking "magic" AWS URL for instance meta-data
                // URL is available only in AWS
                // see: http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-instance-metadata.html

                URL url = new URL(METADATA_URL);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(TIMEOUT);
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
            } catch (IOException exc) {
                isAmazon = false;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException exc) {
                    LOGGER.error(exc.toString(), exc);
                }
            }
        }

        return isAmazon;
    }

    /**
     * Determines the region of the EC2-instance by parsing the instance metadata.
     * For more information on instance metadata take a look at
     * <a href="http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AESDG-chapter-instancedata.html">
     * Instance Metadata and User Data</a></>
     *
     * @return The region as String
     * @throws IOException If the instance meta-data is not available
     */
    String getRegionFromMetadata() throws IOException {

        LOGGER.info("Getting Region from Metadata");
        BufferedReader in = null;
        try {
            String region = "";

            // URL of the instance metadata service
            URL url = new URL(METADATA_URL);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(TIMEOUT);
            String inputLine;

            in = new BufferedReader(
                    new InputStreamReader(
                            conn.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                region = inputLine.substring(0, inputLine.length() - 1);
            }

            LOGGER.info("Region is: <" + region + ">");

            return region;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }


    /**
     * Retrieves the instance-id of the server this software is running on.
     * <p/>
     * The instance-id is aws globally unique - at least for your account.
     * An instance-id looks like this: i-???????? whereas each ? is a hex value,
     * e.g. i-ba842bf5
     *
     * @return instance-id of the current EC2 instance
     * @throws IOException
     */
    public String retrieveInstanceId() {
        try {
            if (instanceId == null) {
                String inputLine;
                URL ec2Metadata = new URL("http://169.254.169.254/latest/meta-data/instance-id");
                URLConnection ec2Md = ec2Metadata.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(ec2Md.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    instanceId = inputLine;
                }
                in.close();
            }
        } catch (IOException ioex) {
            LOGGER.error(ioex.toString(), ioex);
        }
        return instanceId;
    }

    /**
     * Generates an instance-id for own datacenter
     * <p/>
     * This is only used to reproduce the live AWS behavior in the zanox environment.
     * The instance-id will be created randomly and looks like this: dc-???????? whereas each ? is a hex value,
     * e.g. z-ba842bf5
     *
     * @return the generated instance-id of the current server instance
     */
    public String createDcInstanceId() {

        StringBuffer generatedId = new StringBuffer();
        generatedId.append("dc-");

        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int myRandomNumber = random.nextInt(0xF);
            generatedId.append(Integer.toHexString(myRandomNumber));

        }
        return generatedId.toString();
    }
}

