package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(name = "user_name")
    private WebElement usernameInput;

    @FindBy(name = "user_password")
    private WebElement passwordInput;

    @FindBy(id = "submitButton")
    private WebElement loginButton;

    @FindBy(css = ".errorMessage")
    private WebElement errorMessage;

    public LoginPage open(String url) {
        driver.get(url);
        waits.waitForPageReady();
        return this;
    }

    public LoginPage login(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
        waits.waitForPageReady();
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(usernameInput) && isDisplayed(passwordInput) && isDisplayed(loginButton);
    }

    public boolean isErrorVisible() {
        return isDisplayed(errorMessage);
    }

    public boolean isLoggedIn() {
        String currentUrl = driver.getCurrentUrl().toLowerCase();
        return !currentUrl.contains("login") && !isDisplayed(loginButton);
    }
}
