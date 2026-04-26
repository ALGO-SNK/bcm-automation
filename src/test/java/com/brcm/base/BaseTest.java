package com.brcm.base;


import com.brcm.config.ScreenResolutionPreset;
import com.brcm.config.LoginCredentials;
import com.brcm.annotation.TestEnvironment;
import com.brcm.core.DriverFactory;
import com.brcm.pages.DashboardPage;
import com.brcm.pages.LoginPage;
import com.brcm.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Parent class for every UI test.
 *
 * It starts a fresh browser before each test method, exposes `driver` and
 * `baseUrl`, and closes the browser after the test.
 *
 * Beginner usage:
 *   public class MyTest extends BaseTest {
 *       &#064;Test
 *       public void myTest() {
 *           driver.get(baseUrl);
 *           // ...
 *       }
 *   }
 *
 * CLASS-LEVEL DEFAULTS (via @TestDefaults annotation):
 *   &#064;TestDefaults(
 *       baseUrl = "https://mis-teams-team5.bromcom.dev/",
 *       browser = "firefox",
 *       resolution = "1920x1080"
 *   )
 *   public class Team5Tests extends BaseTest { ... }
 *
 * CONFIGURATION PRIORITY (resolution order):
 *   1. Suite parameters (testng.xml) - HIGHEST
 *      <parameter name="BASE_URL" value="https://..."/>
 *      <parameter name="BROWSER" value="chrome|firefox|edge"/>
 *      <parameter name="RESOLUTION" value="1920x1080"/>
 *
 *   2. Class annotation (@TestDefaults) - MIDDLE
 *      Provides default config per test class
 *
 *   3. Global config (src/main/resources/config.properties) - LOWEST
 *      Base configuration for all tests
 *
 * RUNTIME BEHAVIOR:
 *   Local run (class defaults):  mvn test -Dtest=Team5Tests
 *     → Uses @TestDefaults from class
 *
 *   Suite run (suite overrides):  mvn test (with testng.xml)
 *     → Suite parameters override class defaults
 *
 *   No suite, no class defaults:  mvn test -Dtest=SimpleTest
 *     → Falls back to config.properties
 */
public abstract class BaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeSuite(alwaysRun = true)
    public void createOutputDirs() throws Exception {
        Files.createDirectories(Path.of("test-output"));
        Files.createDirectories(Path.of("test-output", "extent"));
        Files.createDirectories(Path.of("test-output", "screenshots"));
        LOG.info("Created test output directories under test-output/");
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"BASE_URL", "BROWSER", "RESOLUTION"})
    public void startBrowser(@Optional String baseUrlParam,
                             @Optional String browserParam,
                             @Optional String resolutionParam) {
        // PRIORITY CHAIN:
        // 1. Suite parameter (testng.xml) - HIGHEST
        // 2. Class annotation (@TestDefaults) - MIDDLE
        // 3. Global config.properties - LOWEST

        baseUrl = firstNonBlank(baseUrlParam,
                getClassDefault("baseUrl", ConfigReader.getOrDefault("base.url", "")));

        String browser = firstNonBlank(browserParam,
                getClassDefault("browser", ConfigReader.getOrDefault("browser", "chrome")));

        String resolution = firstNonBlank(resolutionParam,
                getClassDefault("resolution", "FULL_HD"));

        Dimension size = ScreenResolutionPreset.getDimension(resolution);

        LOG.info("Starting {} with baseUrl={}, browser={}, resolution={}",
                getClass().getSimpleName(), baseUrl, browser, resolution);
        DriverFactory.initDriver(browser, size);
        driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        LOG.info("Closing browser for {}", getClass().getSimpleName());
        DriverFactory.quitDriver();
    }

    private static String firstNonBlank(String a, String b) {
        return (a == null || a.isBlank()) ? b : a;
    }

    /**
     * Gets class-level default from @TestDefaults annotation.
     * Returns the annotation value if present and non-blank, otherwise returns fallback.
     *
     * For resolution: supports both preset names (FULL_HD, HD, QHD) and raw format (1920x1080)
     *
     * Priority:
     * 1. Annotation value (if present and non-blank)
     * 2. Fallback value (config or suite param)
     */
    private String getClassDefault(String property, String fallback) {
        TestEnvironment defaults = this.getClass().getAnnotation(TestEnvironment.class);
        if (defaults == null) {
            return fallback;
        }

        String value = switch (property) {
            case "baseUrl" -> defaults.baseUrl();
            case "browser" -> defaults.browser();
            case "resolution" -> defaults.resolution();
            default -> "";
        };

        // Use annotation value if present and non-blank, otherwise use fallback
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    // ========== LOGIN HELPERS (eliminate boilerplate) ==========

    /**
     * Login as school type or schoolId. One-liner for tests.
     * Usage: DashboardPage dashboard = loginAs("secondary");
     */
    protected DashboardPage loginFor(String schoolTypeOrId) {
        return loginWith(LoginCredentials.forSchool(baseUrl, schoolTypeOrId));
    }

    /**
     * Login with custom credential overrides.
     * Usage: loginAs(Map.of("schoolType", "primary", "username", "alice"))
     */
    protected DashboardPage loginFor(Map<String, Object> config) {
        return loginWith(LoginCredentials.forSchool(baseUrl, config));
    }

    /**
     * Login with default credentials from config.properties.
     */
    protected DashboardPage loginWithDefaults() {
        return loginWith(LoginCredentials.fromProperties(baseUrl));
    }

    private DashboardPage loginWith(LoginCredentials.Resolved creds) {
        return new LoginPage(driver)
                .open(baseUrl)
                .login(creds.schoolId(), creds.username(), creds.password());
    }
}
