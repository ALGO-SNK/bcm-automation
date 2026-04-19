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
}
