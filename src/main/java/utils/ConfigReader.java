package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final Properties PROPERTIES = new Properties();
    private static volatile boolean loaded;

    private ConfigReader() {
    }

    public static String get(String key) {
        loadOnce();
        return PROPERTIES.getProperty(key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        loadOnce();
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            LOG.warn("Invalid integer config for key '{}': '{}'. Using default {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static void loadOnce() {
        if (loaded) {
            return;
        }
        synchronized (ConfigReader.class) {
            if (loaded) {
                return;
            }

            String configuredPath = System.getProperty("config.file");
            if (configuredPath != null && !configuredPath.isBlank()) {
                loadFromFile(Path.of(configuredPath));
            }

            if (PROPERTIES.isEmpty()) {
                loadFromClasspath();
            }

            if (PROPERTIES.isEmpty()) {
                loadFromFile(Path.of("resources", CONFIG_FILE_NAME));
            }

            loaded = true;
        }
    }

    private static void loadFromClasspath() {
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (inputStream != null) {
                PROPERTIES.load(inputStream);
                LOG.info("Loaded config from classpath: {}", CONFIG_FILE_NAME);
            }
        } catch (IOException e) {
            LOG.warn("Unable to load classpath config {}", CONFIG_FILE_NAME, e);
        }
    }

    private static void loadFromFile(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try (InputStream inputStream = Files.newInputStream(path)) {
            PROPERTIES.load(inputStream);
            LOG.info("Loaded config from {}", path.toAbsolutePath());
        } catch (IOException e) {
            LOG.warn("Unable to load config from {}", path.toAbsolutePath(), e);
        }
    }
}
