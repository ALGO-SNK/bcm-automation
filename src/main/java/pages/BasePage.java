package pages;

import core.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
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
        PageFactory.initElements(driver, this);
    }

    protected void click(By locator) {
        actions.click(locator);
    }

    protected void click(WebElement element) {
        actions.click(element);
    }

    protected void type(By locator, String value) {
        actions.type(locator, value);
    }

    protected void type(WebElement element, String value) {
        actions.type(element, value);
    }

    protected String text(By locator) {
        return actions.getText(locator);
    }

    protected String text(WebElement element) {
        return actions.getText(element);
    }

    protected boolean isDisplayed(By locator) {
        return actions.isDisplayed(locator);
    }

    protected boolean isDisplayed(WebElement element) {
        return actions.isDisplayed(element);
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
