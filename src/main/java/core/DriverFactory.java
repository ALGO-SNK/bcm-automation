package core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import utils.ConfigReader;

import java.time.Duration;
import java.util.Locale;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void initDriver() {
        if (TL_DRIVER.get() != null) {
            return;
        }

        String browser = ConfigReader.getOrDefault("browser", "chrome")
                .trim()
                .toLowerCase(Locale.ROOT);
        boolean headless = ConfigReader.getBoolean("headless", false);
        WebDriver driver;

        switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                driver = new FirefoxDriver(options);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                driver = new EdgeDriver(options);
            }
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                }
                driver = new ChromeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        int implicitWait = ConfigReader.getInt("implicit.wait.seconds", 5);
        int pageLoadTimeout = ConfigReader.getInt("page.load.timeout.seconds", 30);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        if (ConfigReader.getBoolean("window.maximize", true) && !headless) {
            driver.manage().window().maximize();
        }

        TL_DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        return TL_DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = TL_DRIVER.get();
        if (driver != null) {
            driver.quit();
            TL_DRIVER.remove();
        }
    }
}
