package com.brcm;

import com.brcm.base.BaseTest;
import com.brcm.config.LoginCredentials;
import com.brcm.annotation.TestEnvironment;
import com.brcm.pages.DashboardPage;
import com.brcm.pages.LoginPage;
import com.brcm.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Example UI tests. A good template to copy when writing your first test.
 *
 * Demonstrates:
 *   1. Class-level defaults via @TestDefaults
 *   2. Default login: loginWithDefaults()
 *   3. School type or schoolId: loginAs("secondary") / loginAs("283001")
 *   4. Custom map: loginAs(Map.of("schoolType", "primary", "username", "alice"))
 *   5. Server-keyed overrides
 *   6. Fluent builder: LoginCredentials.custom(baseUrl)....resolve()
 */
@TestEnvironment(
        baseUrl = "https://mis-teams-team5.bromcom.dev/",
        browser = "chrome",
        resolution = "FULL_HD"
)
public class LoginTest extends BaseTest {

    @Test(groups = {"ui", "smoke"})
    public void loginPageShouldLoad() {
        LoginPage loginPage = new LoginPage(driver).open(baseUrl);
        Assert.assertTrue(loginPage.isLoginPageReady(), "Login page did not load.");
    }

    /** Login with default credentials from config.properties. */
    @Test(groups = {"ui", "smoke"}, retryAnalyzer = RetryAnalyzer.class)
    public void loginWithDefaultValidCredentials() {
        DashboardPage dashboard = loginWithDefaults();
        Assert.assertTrue(dashboard.isLoaded(), "Dashboard did not load after login");
    }

    /** Login by school type. */
    @Test(groups = {"ui"})
    public void loginWithSchoolType() {
        DashboardPage dashboard = loginFor("secondary");
        Assert.assertTrue(dashboard.isLoaded(), "Failed to login with school type 'secondary'");
    }

    /** Login with explicit schoolId. */
    @Test(groups = {"ui"})
    public void loginWithExplicitSchoolId() {
        DashboardPage dashboard = loginFor("283001");
        Assert.assertTrue(dashboard.isLoaded(), "Failed to login with schoolId '283001'");
    }

    /** Login with custom username via map. */
    @Test(groups = {"ui"})
    public void loginWithCustomUsername() {
        DashboardPage dashboard = loginFor(Map.of(
                "schoolType", "primary",
                "username", "brcm"
        ));
        Assert.assertTrue(dashboard.isLoaded(), "Failed to login with custom username");
    }

    /** Login using the fluent builder. */
    @Test(groups = {"ui"})
    public void loginWithAdvancedConfig() {
        LoginCredentials.Resolved creds = LoginCredentials.builder(baseUrl)
                .withSchoolType("primary")
                .withUsername("alice")
                .resolve();

        DashboardPage dashboard = new LoginPage(driver)
                .open(baseUrl)
                .login(creds.schoolId(), creds.username(), creds.password());

        Assert.assertTrue(dashboard.isLoaded(), "Failed to login with advanced config");
    }

    /** Login with per-server overrides for multi-environment testing. */
    @Test(groups = {"ui"})
    public void loginWithServerOverrides() {
        DashboardPage dashboard = loginFor(Map.of(
                "ALL", Map.of("username", "globaluser"),
                "RELEASE", Map.of("schoolType", "secondary"),
                "TEAM5", Map.of("schoolType", "secondary")
        ));
        Assert.assertTrue(dashboard.isLoaded(), "Failed to login with server-specific overrides");
    }

    @Test(groups = {"ui"})
    public void invalidCredentialsShouldShowError() {
        LoginCredentials.Resolved creds = LoginCredentials.builder(baseUrl)
                .withSchoolType("secondary")
                .withUsername("wrong-user")
                .withPassword("wrong-pass")
                .resolve();

        LoginPage loginPage = new LoginPage(driver).open(baseUrl);
        loginPage.login(creds.schoolId(), creds.username(), creds.password());
        Assert.assertTrue(loginPage.isErrorVisible(),
                "Expected an error message for invalid credentials.");
    }
}
