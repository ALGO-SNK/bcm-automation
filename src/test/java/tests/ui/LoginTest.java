package tests.ui;

import core.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.ConfigReader;
import utils.RetryAnalyzer;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        loginPage = new LoginPage();
    }

    @Test(groups = {"ui", "smoke"})
    public void loginPageShouldLoad() {
        requireConfigValue("ui.base.url");
        loginPage.open(ConfigReader.get("ui.base.url"));
        Assert.assertTrue(loginPage.isLoaded(), "Login page did not load correctly.");
    }

    @Test(groups = {"ui"}, retryAnalyzer = RetryAnalyzer.class)
    public void invalidLoginShouldShowError() {
        requireConfigValue("ui.base.url");
        loginPage.open(ConfigReader.get("ui.base.url"))
                .login("invalid.user", "invalid.password");
        Assert.assertTrue(loginPage.isErrorVisible(), "Expected an error message for invalid login.");
    }
}
