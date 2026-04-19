package utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitUtils {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        int timeout = ConfigReader.getInt("explicit.wait.seconds", 15);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public WebElement visibilityOf(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> visibilityOfAll(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement visibilityOf(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement presenceOf(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public List<WebElement> presenceOfAll(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement clickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public boolean invisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public Alert alertIsPresent() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    public boolean textToBePresentInElement(By locator, String expectedText) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }

    public boolean urlContains(String text) {
        return wait.until(ExpectedConditions.urlContains(text));
    }

    public boolean titleContains(String text) {
        return wait.until(ExpectedConditions.titleContains(text));
    }

    public void frameToBeAvailableAndSwitchToIt(By locator) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    public void frameToBeAvailableAndSwitchToIt(WebElement frameElement) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
    }

    public boolean numberOfWindowsToBe(int expectedCount) {
        return wait.until(ExpectedConditions.numberOfWindowsToBe(expectedCount));
    }

    public void waitForPageReady() {
        wait.until(driver -> {
            Object readyState = ((JavascriptExecutor) driver).executeScript("return document.readyState");
            return "complete".equals(readyState);
        });
    }

    public boolean isVisible(By locator) {
        try {
            visibilityOf(locator);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isVisible(WebElement element) {
        try {
            visibilityOf(element);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
