package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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

    public WebElement visibilityOf(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
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
