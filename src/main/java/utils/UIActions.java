package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class UIActions {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    public UIActions(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public void click(By locator) {
        waitUtils.clickable(locator).click();
    }

    public void click(WebElement element) {
        waitUtils.clickable(element).click();
    }

    public void type(By locator, String text) {
        WebElement element = waitUtils.visibilityOf(locator);
        element.clear();
        element.sendKeys(text);
    }

    public void type(WebElement element, String text) {
        WebElement visibleElement = waitUtils.visibilityOf(element);
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }

    public String getText(By locator) {
        return waitUtils.visibilityOf(locator).getText().trim();
    }

    public String getText(WebElement element) {
        return waitUtils.visibilityOf(element).getText().trim();
    }

    public boolean isDisplayed(By locator) {
        return waitUtils.isVisible(locator);
    }

    public boolean isDisplayed(WebElement element) {
        return waitUtils.isVisible(element);
    }

    public void selectByVisibleText(By locator, String visibleText) {
        Select select = new Select(waitUtils.visibilityOf(locator));
        select.selectByVisibleText(visibleText);
    }

    public void scrollIntoView(By locator) {
        WebElement element = waitUtils.visibilityOf(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public void scrollIntoView(WebElement element) {
        WebElement visibleElement = waitUtils.visibilityOf(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", visibleElement);
    }
}
