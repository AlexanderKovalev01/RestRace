package org.sk.races.rest.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static Configuration instance;
    private Properties properties;

    private Configuration() {
        InputStream is = Configuration.class.getClassLoader().getResourceAsStream("raceapp.properties");
        properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public Properties getProperties() {
        return properties;
    }

}
