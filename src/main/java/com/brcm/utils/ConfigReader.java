package com.brcm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads values from property files on the classpath (src/main/resources).
 *
 * Loads config.properties + credentials.properties once, lazily.
 * Command-line overrides: -Dkey=value  (System properties always win).
 *
 * Beginner usage:
 *   String browser = ConfigReader.get("browser");
 *   int timeout    = ConfigReader.getInt("explicit.wait.seconds", 30);
 */
public final class ConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);
    private static final String[] FILES = {"config.properties", "credentials.properties"};
    private static final Properties PROPS = new Properties();
    private static volatile boolean loaded;

    private ConfigReader() {}

    public static String get(String key) {
        loadOnce();
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        return PROPS.getProperty(key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            LOG.warn("Invalid integer for '{}': '{}'. Using default {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) return defaultValue;
        return Boolean.parseBoolean(value.trim());
    }

    private static void loadOnce() {
        if (loaded) return;
        synchronized (ConfigReader.class) {
            if (loaded) return;
            for (String file : FILES) {
                loadFromClasspath(file);
            }
            loaded = true;
        }
    }

    private static void loadFromClasspath(String fileName) {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in != null) {
                PROPS.load(in);
                LOG.info("Loaded {}", fileName);
            } else {
                LOG.warn("{} not found on classpath (src/main/resources)", fileName);
            }
        } catch (IOException e) {
            LOG.warn("Unable to load {}", fileName, e);
        }
    }
}
