package eu.hansolo.tilesfx;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Tiles localization helper. Use it to load your own localization
 */
public class TilesLocalization {
    private static final String DEFAULT_EN_LOCALIZATION_PROPERTIES = "eu/hansolo/tilesfx/en.properties";

    private static Properties properties;

    /**
     * Loads localization properties for the specified properties file name
     *
     * @param propertiesFileName File name
     */
    public static void load(String propertiesFileName) {
        InputStream propertiesInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFileName);
        if (propertiesInputStream != null) {
            try {
                if (properties == null)
                    properties = new Properties();
                properties.load(new InputStreamReader(propertiesInputStream, StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.err.println("Can't load properties: " + propertiesFileName);
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns localization value by key
     *
     * @param key Key
     */
    public static String property(String key) {
        if (properties == null)
            load(DEFAULT_EN_LOCALIZATION_PROPERTIES);
        return properties.getProperty(key);
    }
}
