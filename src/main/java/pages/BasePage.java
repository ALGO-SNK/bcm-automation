package pages;

import core.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.UIActions;
import utils.WaitUtils;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final UIActions actions;
    protected final WaitUtils waits;

    protected BasePage() {
        this(DriverFactory.getDriver());
    }

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.actions = new UIActions(driver);
        this.waits = new WaitUtils(driver);
    }

    protected void click(By locator) {
        actions.click(locator);
    }

    protected void type(By locator, String value) {
        actions.type(locator, value);
    }

    protected String text(By locator) {
        return actions.getText(locator);
    }

    protected boolean isDisplayed(By locator) {
        return actions.isDisplayed(locator);
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
