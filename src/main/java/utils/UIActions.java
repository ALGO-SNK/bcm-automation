package utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

public class UIActions {

    private final WebDriver driver;
    private final WaitUtils waitUtils;
    private final Actions seleniumActions;

    public UIActions(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.seleniumActions = new Actions(driver, Duration.ofMillis(300));
    }

    // ---------------------------
    // Basic element interaction
    // ---------------------------
    public void click(By locator) {
        waitUtils.clickable(locator).click();
    }

    public void click(WebElement element) {
        waitUtils.clickable(element).click();
    }

    public void clear(By locator) {
        waitUtils.visibilityOf(locator).clear();
    }

    public void clear(WebElement element) {
        waitUtils.visibilityOf(element).clear();
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

    public void append(By locator, String text) {
        waitUtils.visibilityOf(locator).sendKeys(text);
    }

    public void append(WebElement element, String text) {
        waitUtils.visibilityOf(element).sendKeys(text);
    }

    public void pressKey(By locator, Keys key) {
        waitUtils.visibilityOf(locator).sendKeys(key);
    }

    public void pressKey(WebElement element, Keys key) {
        waitUtils.visibilityOf(element).sendKeys(key);
    }

    public void submit(By locator) {
        waitUtils.visibilityOf(locator).submit();
    }

    public void submit(WebElement element) {
        waitUtils.visibilityOf(element).submit();
    }

    public String getText(By locator) {
        return waitUtils.visibilityOf(locator).getText().trim();
    }

    public String getText(WebElement element) {
        return waitUtils.visibilityOf(element).getText().trim();
    }

    public String getAttribute(By locator, String attribute) {
        return waitUtils.visibilityOf(locator).getAttribute(attribute);
    }

    public String getAttribute(WebElement element, String attribute) {
        return waitUtils.visibilityOf(element).getAttribute(attribute);
    }

    public String getValue(By locator) {
        return getAttribute(locator, "value");
    }

    public String getValue(WebElement element) {
        return getAttribute(element, "value");
    }

    public boolean isDisplayed(By locator) {
        return waitUtils.isVisible(locator);
    }

    public boolean isDisplayed(WebElement element) {
        return waitUtils.isVisible(element);
    }

    public boolean isEnabled(By locator) {
        return waitUtils.visibilityOf(locator).isEnabled();
    }

    public boolean isEnabled(WebElement element) {
        return waitUtils.visibilityOf(element).isEnabled();
    }

    public boolean isSelected(By locator) {
        return waitUtils.visibilityOf(locator).isSelected();
    }

    public boolean isSelected(WebElement element) {
        return waitUtils.visibilityOf(element).isSelected();
    }

    public List<WebElement> findVisibleElements(By locator) {
        return waitUtils.visibilityOfAll(locator);
    }

    // ---------------------------
    // Select dropdown actions
    // ---------------------------
    public void selectByVisibleText(By locator, String visibleText) {
        Select select = new Select(waitUtils.visibilityOf(locator));
        select.selectByVisibleText(visibleText);
    }

    public void selectByVisibleText(WebElement element, String visibleText) {
        Select select = new Select(waitUtils.visibilityOf(element));
        select.selectByVisibleText(visibleText);
    }

    public void selectByValue(By locator, String value) {
        Select select = new Select(waitUtils.visibilityOf(locator));
        select.selectByValue(value);
    }

    public void selectByValue(WebElement element, String value) {
        Select select = new Select(waitUtils.visibilityOf(element));
        select.selectByValue(value);
    }

    public void selectByIndex(By locator, int index) {
        Select select = new Select(waitUtils.visibilityOf(locator));
        select.selectByIndex(index);
    }

    public void selectByIndex(WebElement element, int index) {
        Select select = new Select(waitUtils.visibilityOf(element));
        select.selectByIndex(index);
    }

    public String getSelectedOptionText(By locator) {
        Select select = new Select(waitUtils.visibilityOf(locator));
        return select.getFirstSelectedOption().getText().trim();
    }

    public String getSelectedOptionText(WebElement element) {
        Select select = new Select(waitUtils.visibilityOf(element));
        return select.getFirstSelectedOption().getText().trim();
    }

    public List<WebElement> getAllOptions(By locator) {
        Select select = new Select(waitUtils.visibilityOf(locator));
        return select.getOptions();
    }

    public List<WebElement> getAllOptions(WebElement element) {
        Select select = new Select(waitUtils.visibilityOf(element));
        return select.getOptions();
    }

    public void deselectAll(By locator) {
        Select select = new Select(waitUtils.visibilityOf(locator));
        if (select.isMultiple()) {
            select.deselectAll();
        }
    }

    public void deselectAll(WebElement element) {
        Select select = new Select(waitUtils.visibilityOf(element));
        if (select.isMultiple()) {
            select.deselectAll();
        }
    }

    // ---------------------------
    // Selenium Actions class
    // ---------------------------
    public void hover(By locator) {
        WebElement element = waitUtils.visibilityOf(locator);
        seleniumActions.moveToElement(element).perform();
    }

    public void hover(WebElement element) {
        seleniumActions.moveToElement(waitUtils.visibilityOf(element)).perform();
    }

    public void doubleClick(By locator) {
        WebElement element = waitUtils.clickable(locator);
        seleniumActions.doubleClick(element).perform();
    }

    public void doubleClick(WebElement element) {
        seleniumActions.doubleClick(waitUtils.clickable(element)).perform();
    }

    public void rightClick(By locator) {
        WebElement element = waitUtils.clickable(locator);
        seleniumActions.contextClick(element).perform();
    }

    public void rightClick(WebElement element) {
        seleniumActions.contextClick(waitUtils.clickable(element)).perform();
    }

    public void dragAndDrop(By source, By target) {
        WebElement sourceElement = waitUtils.visibilityOf(source);
        WebElement targetElement = waitUtils.visibilityOf(target);
        seleniumActions.dragAndDrop(sourceElement, targetElement).perform();
    }

    public void dragAndDrop(WebElement source, WebElement target) {
        seleniumActions.dragAndDrop(waitUtils.visibilityOf(source), waitUtils.visibilityOf(target)).perform();
    }

    public void clickAndHold(By locator) {
        seleniumActions.clickAndHold(waitUtils.visibilityOf(locator)).perform();
    }

    public void clickAndHold(WebElement element) {
        seleniumActions.clickAndHold(waitUtils.visibilityOf(element)).perform();
    }

    public void release(By locator) {
        seleniumActions.release(waitUtils.visibilityOf(locator)).perform();
    }

    public void release(WebElement element) {
        seleniumActions.release(waitUtils.visibilityOf(element)).perform();
    }

    public void sendKeysWithActions(By locator, CharSequence... keys) {
        seleniumActions.moveToElement(waitUtils.visibilityOf(locator)).click().sendKeys(keys).perform();
    }

    public void sendKeysWithActions(WebElement element, CharSequence... keys) {
        seleniumActions.moveToElement(waitUtils.visibilityOf(element)).click().sendKeys(keys).perform();
    }

    // ---------------------------
    // JavaScript actions
    // ---------------------------
    public Object executeJs(String script, Object... args) {
        return jsExecutor().executeScript(script, args);
    }

    public void jsClick(By locator) {
        WebElement element = waitUtils.visibilityOf(locator);
        jsExecutor().executeScript("arguments[0].click();", element);
    }

    public void jsClick(WebElement element) {
        jsExecutor().executeScript("arguments[0].click();", waitUtils.visibilityOf(element));
    }

    public void setValueByJs(By locator, String value) {
        WebElement element = waitUtils.visibilityOf(locator);
        jsExecutor().executeScript("arguments[0].value=arguments[1];", element, value);
    }

    public void setValueByJs(WebElement element, String value) {
        jsExecutor().executeScript("arguments[0].value=arguments[1];", waitUtils.visibilityOf(element), value);
    }

    public void highlight(By locator) {
        WebElement element = waitUtils.visibilityOf(locator);
        jsExecutor().executeScript("arguments[0].style.border='2px solid red';", element);
    }

    public void highlight(WebElement element) {
        jsExecutor().executeScript("arguments[0].style.border='2px solid red';", waitUtils.visibilityOf(element));
    }

    public void scrollIntoView(By locator) {
        WebElement element = waitUtils.visibilityOf(locator);
        jsExecutor().executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public void scrollIntoView(WebElement element) {
        WebElement visibleElement = waitUtils.visibilityOf(element);
        jsExecutor().executeScript("arguments[0].scrollIntoView({block: 'center'});", visibleElement);
    }

    public void scrollToTop() {
        jsExecutor().executeScript("window.scrollTo(0, 0);");
    }

    public void scrollToBottom() {
        jsExecutor().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void scrollBy(int x, int y) {
        jsExecutor().executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    // ---------------------------
    // Alerts
    // ---------------------------
    public Alert waitForAlert() {
        return waitUtils.alertIsPresent();
    }

    public String getAlertText() {
        return waitForAlert().getText();
    }

    public void acceptAlert() {
        waitForAlert().accept();
    }

    public void dismissAlert() {
        waitForAlert().dismiss();
    }

    public void typeInAlert(String text) {
        Alert alert = waitForAlert();
        alert.sendKeys(text);
    }

    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    // ---------------------------
    // Frame and window helpers
    // ---------------------------
    public void switchToFrame(By frameLocator) {
        waitUtils.frameToBeAvailableAndSwitchToIt(frameLocator);
    }

    public void switchToFrame(WebElement frameElement) {
        waitUtils.frameToBeAvailableAndSwitchToIt(frameElement);
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
    }

    public void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    public String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }

    public void closeCurrentWindow() {
        driver.close();
    }

    private JavascriptExecutor jsExecutor() {
        return (JavascriptExecutor) driver;
    }
}
