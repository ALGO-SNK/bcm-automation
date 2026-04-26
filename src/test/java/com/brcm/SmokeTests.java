package com.brcm;

import com.brcm.base.BaseTest;
import com.brcm.annotation.TestEnvironment;
import com.brcm.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Smoke tests - Quick validation on TEAM0 environment.
 *
 * Class defaults:
 *   - baseUrl: https://mis-teams-team0.bromcom.dev/
 *   - browser: chrome (system default)
 *   - resolution: system default (maximize window)
 *
 * Local run: mvn test -Dtest=SmokeTests
 *   → Uses TEAM0 environment from class default
 *
 * Suite run: mvn test (with testng.xml)
 *   → Suite parameters override class default (if provided)
 */
@TestEnvironment(
        baseUrl = "https://mis-teams-team0.bromcom.dev/"
)
public class SmokeTests extends BaseTest {

    @Test(groups = {"smoke"})
    public void loginPageLoadsSmokeTest() {
        LoginPage loginPage = new LoginPage(driver).open(baseUrl);
        Assert.assertTrue(loginPage.isLoginPageReady(), "Login page should load on TEAM0");
    }
}
