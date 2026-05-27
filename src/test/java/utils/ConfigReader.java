package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility to read framework configurations from config.properties.
 */
public class ConfigReader {
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to load config.properties file.");
            e.printStackTrace();
            throw new RuntimeException("Could not load config.properties file.", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getBrowser() {
        return getProperty("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless"));
    }

    public static String getUrl() {
        return getProperty("url");
    }

    public static int getTimeout() {
        return Integer.parseInt(getProperty("timeout"));
    }

    public static int getWidth() {
        return Integer.parseInt(getProperty("width"));
    }

    public static int getHeight() {
        return Integer.parseInt(getProperty("height"));
    }
}
