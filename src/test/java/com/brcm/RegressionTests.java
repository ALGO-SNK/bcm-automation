package com.brcm;

import com.brcm.base.BaseTest;
import com.brcm.config.LoginCredentials;
import com.brcm.pages.DashboardPage;
import com.brcm.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Regression tests - Environment-agnostic, runs on any server.
 *
 * NO class defaults (@TestDefaults) - Uses configuration priority:
 *   1. Suite parameters (if provided)
 *   2. Global config.properties (fallback)
 */
public class RegressionTests extends BaseTest {

    @Test(groups = {"regression"})
    public void multiEnvironmentLoginTest() {
        DashboardPage dashboard = loginFor(Map.of(
                "ALL", Map.of("username", "testuser"),
                "TEAM0", Map.of("schoolType", "secondary"),
                "TEAM5", Map.of("schoolType", "primary"),
                "RELEASE", Map.of("schoolType", "secondary")
        ));

        Assert.assertTrue(dashboard.isLoaded(),
                "Should login successfully on " + LoginCredentials.Server.getServerForUrl(baseUrl) + " environment");
    }

    @Test(groups = {"regression"})
    public void navigateToDashboardTest() {
        LoginPage loginPage = new LoginPage(driver).open(baseUrl);
        Assert.assertTrue(loginPage.isLoginPageReady(),
                "Login page should be accessible on " + baseUrl);
    }
}
