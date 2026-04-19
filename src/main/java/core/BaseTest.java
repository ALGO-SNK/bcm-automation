package core;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public abstract class BaseTest {

    protected ApiClient apiClient;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() throws Exception {
        Files.createDirectories(Path.of("test-output", "extent"));
        Files.createDirectories(Path.of("test-output", "screenshots"));
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        apiClient = new ApiClient(ConfigReader.getOrDefault("api.base.url", "https://reqres.in/api"));
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        if (requiresUI()) {
            DriverFactory.initDriver();
            if (openBaseUrlOnLaunch()) {
                String baseUrl = ConfigReader.getOrDefault("ui.base.url", "").trim();
                if (!baseUrl.isEmpty()) {
                    driver().get(baseUrl);
                }
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (requiresUI()) {
            DriverFactory.quitDriver();
        }
    }

    protected boolean requiresUI() {
        return true;
    }

    protected boolean openBaseUrlOnLaunch() {
        return false;
    }

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

    protected void requireConfigValue(String key) {
        String value = ConfigReader.get(key);
        if (value == null || value.isBlank()) {
            throw new SkipException("Missing required config value: " + key);
        }

        String normalized = value.toLowerCase(Locale.ROOT);
        if (normalized.contains("change-me")
                || normalized.contains("your-")
                || normalized.contains("placeholder")) {
            throw new SkipException("Update config value before running this test: " + key);
        }
    }
}
