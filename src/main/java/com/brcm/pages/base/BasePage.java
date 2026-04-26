package com.brcm.pages.base;

import com.brcm.core.DriverFactory;
import com.brcm.utils.UIActions;
import com.brcm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * Parent class for every Page Object.
 *
 * Beginner usage:
 *   public class MyPage extends BasePage {
 *       &#064;FindBy(id = "submit") WebElement submitButton;
 *
 *       public MyPage(WebDriver driver) { super(driver); }
 *
 *       public void submit() { actions.click(submitButton); }
 *   }
 *
 * Each page gets:
 *   driver  — the Selenium WebDriver
 *   actions — helpers like click, type, select  (see UIActions)
 *   waits   — helpers like waitForPageReady     (see WaitUtils)
 */
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
    protected enum LocatorType {
        ID, NAME, CSS, XPATH, CLASS, TAG, LINKTEXT, PARTIALLINKTEXT;

        public By build(String value) {
            return switch (this) {
                case ID -> By.id(value);
                case NAME -> By.name(value);
                case CSS -> By.cssSelector(value);
                case XPATH -> By.xpath(value);
                case CLASS -> By.className(value);
                case TAG -> By.tagName(value);
                case LINKTEXT -> By.linkText(value);
                case PARTIALLINKTEXT -> By.partialLinkText(value);
            };
        }
    }

    protected By geLocator(LocatorType locatorType, String value) {
        return locatorType.build(value);
    }
}
