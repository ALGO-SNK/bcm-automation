package com.brcm.core;

import com.brcm.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Creates and stores a WebDriver per thread.
 *
 * Beginner usage (normally called by UIBaseTest, not your test):
 *   DriverFactory.initDriver();            // uses browser from config.properties
 *   WebDriver driver = DriverFactory.getDriver();
 *   DriverFactory.quitDriver();            // in @AfterMethod
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    private DriverFactory() {}

    /** Initializes the driver using the browser from config.properties. */
    public static void initDriver() {
        initDriver(ConfigReader.getOrDefault("browser", "chrome"), null);
    }

    /**
     * Initializes the driver with the given browser and optional window size.
     * If a driver already exists on this thread, does nothing.
     */
    public static void initDriver(String browser, Dimension windowSize) {
        if (TL_DRIVER.get() != null) return;

        String name = (browser == null || browser.isBlank())
                ? "chrome"
                : browser.trim().toLowerCase(Locale.ROOT);
        boolean headless = ConfigReader.getBoolean("headless", false);
        int width = windowSize == null ? 1920 : windowSize.getWidth();
        int height = windowSize == null ? 1080 : windowSize.getHeight();

        LOG.info("Initializing WebDriver: browser={}, headless={}, size={}x{}",
                name, headless, width, height);
        WebDriver driver = switch (name) {
            case "firefox" -> createFirefox(headless, width, height);
            case "edge"    -> createEdge(headless, width, height);
            case "chrome"  -> createChrome(headless, width, height);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser
                    + " (supported: chrome, firefox, edge)");
        };

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicit.wait.seconds", 5)));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getInt("page.load.timeout.seconds", 30)));

        if (!headless) {
            if (windowSize != null) {
                driver.manage().window().setSize(windowSize);
            } else if (ConfigReader.getBoolean("window.maximize", true)) {
                driver.manage().window().maximize();
            }
        }

        TL_DRIVER.set(driver);
        LOG.info("WebDriver ready: {}", driver.getClass().getSimpleName());
    }

    public static WebDriver getDriver() {
        return TL_DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = TL_DRIVER.get();
        if (driver != null) {
            LOG.info("Shutting down WebDriver: {}", driver.getClass().getSimpleName());
            driver.quit();
            TL_DRIVER.remove();
        }
    }

    private static WebDriver createChrome(boolean headless, int w, int h) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        // Suppress Chrome's "Save password" / "Change your password" / leaked-credential popups.
        options.addArguments(
                "--disable-save-password-bubble",
                "--disable-features=PasswordLeakDetection,PasswordManagerOnboarding," +
                        "AutofillServerCommunication,PasswordCheck,SafeBrowsingEnhancedProtection",
                "--disable-notifications",
                "--disable-infobars"
        );
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("autofill.profile_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=" + w + "," + h);
        }
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefox(boolean headless, int w, int h) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
            options.addArguments("--width=" + w);
            options.addArguments("--height=" + h);
        }
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdge(boolean headless, int w, int h) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=" + w + "," + h);
        }
        return new EdgeDriver(options);
    }
}
