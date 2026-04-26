package com.brcm.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * Thin wrapper around {@link WebDriverWait}. All methods throw a TimeoutException
 * if the condition is not met within the configured explicit wait.
 *
 * Beginner usage:
 *   waits.visibilityOf(element);           // wait until element is visible
 *   waits.clickable(element);              // wait until element can be clicked
 *   waits.waitForPageReady();              // wait until document.readyState == complete
 *
 * Timeout is taken from config.properties (explicit.wait.seconds).
 */
public class WaitUtils {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        int timeout = ConfigReader.getInt("explicit.wait.seconds", 30);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public WebElement visibilityOf(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement visibilityOf(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> visibilityOfAll(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement presenceOf(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement clickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean invisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public Alert alertIsPresent() {
        return wait.until(ExpectedConditions.alertIsPresent());
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

    public void waitUntilTrue(Supplier<Boolean> condition){
        wait.until(d -> condition.get());
    }

    public void waitUntilFalse(Supplier<Boolean> condition){
        wait.until(d -> !condition.get());
    }

    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Waits until document.readyState == "complete". */
    public void waitForPageReady() {
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
    }
}
