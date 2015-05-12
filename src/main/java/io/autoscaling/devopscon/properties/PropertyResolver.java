package io.autoscaling.devopscon.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for resolving properties file by the given file path.
 *
 * @author Sascha Möllering
 */
public abstract class PropertyResolver {

    private static final Logger LOGGER = LogManager.getLogger(PropertyResolver.class);

    /**
     * Resolves the *.properties file from the classpath by the given file name.
     *
     * @param propFileName *.properties file name
     * @return loaded Properties instance
     */
    Properties getPropertiesFromClasspath(String propFileName) {
        Properties props = new Properties();
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName)) {

            if (inputStream == null) {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            props.load(inputStream);

            return props;
        } catch (IOException exc) {
            LOGGER.error("Not able to read properties", exc);
        }

        return null;
    }
}
