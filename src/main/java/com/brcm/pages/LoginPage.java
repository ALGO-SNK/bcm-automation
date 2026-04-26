package com.brcm.pages;

import com.brcm.annotation.TestEnvironment;
import com.brcm.pages.base.BasePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the login screen.
 *
 * Beginner usage:
 *   LoginPage loginPage = new LoginPage(driver);
 *   DashboardPage dashboard = loginPage
 *       .open("https://...")
 *       .login("schoolId", "user", "pass");
 */

@TestEnvironment(
        baseUrl = "https://mis-team5.bromcom.dev",
        browser = "chrome",
        resolution = "FULL_HD"
)
public class LoginPage extends BasePage {

    private static final Logger LOG = LoggerFactory.getLogger(LoginPage.class);

    @FindBy(id = "SchoolIDTextBox") private WebElement schoolIdInput;
    @FindBy(id = "UsernameTextBox") private WebElement usernameInput;
    @FindBy(id = "PasswordTextBox") private WebElement passwordInput;
    @FindBy(id = "LoginButton") private WebElement loginButton;
    @FindBy(id = "ErrorValidationSummary") private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Opens the login page URL and waits for the page to finish loading. */
    public LoginPage open(String url) {
        LOG.info("Opening login page: {}", url);
        driver.get(url);
        waits.waitForPageReady();
        return this;
    }

    /**
     * Fills in the form and clicks login.
     * Pass an empty schoolId ("") if the site doesn't show that field.
     */
    public DashboardPage login(String schoolId, String username, String password) {
        LOG.info("Submitting login form with schoolId={}, username={}",
                (schoolId == null || schoolId.isBlank()) ? "<blank>" : schoolId,
                (username == null || username.isBlank()) ? "<blank>" : username);
        if (schoolId != null && !schoolId.isBlank()) {
            actions.type(schoolIdInput, schoolId);
        }
        waits.waitUntilTrue(this::isLoginPageReady);
        actions.type(usernameInput, username);
        actions.type(passwordInput, password);
        actions.click(loginButton);
        waits.waitForPageReady();
        waits.waitUntilFalse(this::isLoginPageReady);
        return new DashboardPage(driver);
    }

    public boolean isLoginPageReady() {
        return actions.isDisplayed(usernameInput)
                && actions.isDisplayed(passwordInput)
                && actions.isDisplayed(loginButton);
    }

    public boolean isErrorVisible() {
        return actions.isDisplayed(errorMessage);
    }

    public boolean isLoggedIn() {
        String url = driver.getCurrentUrl();
        return url != null && !url.toLowerCase().contains("login");
    }
}
