package com.brcm.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

/**
 * Common UI actions, built on top of WaitUtils so that every action waits
 * for the element to be ready.
 * <p>
 * Use with @FindBy-annotated WebElement fields:
 * actions.click(loginButton);
 * actions.type(usernameInput, "alice");
 * <p>
 * For locator-based lookups (no Page Object), use {@link #click(By)} etc.
 */
public class UIActions {

    private final WebDriver driver;
    private final WaitUtils waits;
    private final Actions seleniumActions;

    public UIActions(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitUtils(driver);
        this.seleniumActions = new Actions(driver, Duration.ofMillis(300));
    }

    // ---------- Internal helpers ----------

    private static String describe(Object target) {
        if (target == null) return "<no target>";
        String s = String.valueOf(target);
        // Trim WebElement toString: "[[ChromeDriver: ...] -> id: foo]" -> "id: foo"
        int arrow = s.lastIndexOf("-> ");
        if (arrow > -1) s = s.substring(arrow + 3).replaceAll("[\\[\\]]", "");
        return s.length() > 80 ? s.substring(0, 80) + "…" : s;
    }

    private static void autoLog(String action, Object target) {
        LogUtils.action(action + " → " + describe(target));
    }

    private static void autoLog(String action, Object target, String value) {
        LogUtils.action(action + " → " + describe(target) + " [" + maskIfSensitive(target, value) + "]");
    }

    private static String maskIfSensitive(Object target, String value) {
        if (value == null) {
            return "<null>";
        }

        String description = describe(target).toLowerCase();
        if (description.contains("password") || description.contains("secret")) {
            return "********";
        }

        return value;
    }

    private FailureContextException failureException(
            String message,
            Exception cause,
            WebElement element,
            String action
    ) {
        FailureContextCapture ctx = null;
        try {
            if (element != null) {
                ctx = FailureContextCapture.from(element)
                        .withAction(action)
                        .build();
            }
        } catch (Exception ignored) {
            // Do not mask original failure if context building also fails
        }
        return new FailureContextException(message, cause, ctx);
    }

    private FailureContextException failureException(
            String errorMessage,
            Exception cause,
            WebElement targetElement,
            String performedAction,
            String locatorDescription
    ) {
        FailureContextCapture ctx = null;
        try {
            if (targetElement != null) {
                FailureContextCapture.Builder builder = FailureContextCapture.from(targetElement)
                        .withAction(performedAction);

                if (locatorDescription != null) {
                    builder.withLocator(locatorDescription);
                }

                ctx = builder.build();
            }
        } catch (Exception ignored) {
            // Do not mask original failure if context building also fails
        }
        return new FailureContextException(errorMessage, cause, ctx);
    }

    private WebElement safeFind(By locator) {
        try {
            return driver.findElement(locator);
        } catch (Exception ignored) {
            return null;
        }
    }

    // ---------- Clicks & typing ----------

    /**
     * Click a web element with failure context capture.
     * If click fails, attaches element details to exception.
     */
    public void click(WebElement element) {
        autoLog("Click", element);
        try {
            waits.clickable(element).click();
        } catch (Exception e) {
            throw failureException("Failed to click element", e, element, "click");
        }
    }

    /**
     * Click by locator with failure context capture.
     */
    public void click(By locator) {
        autoLog("Click", locator);
        try {
            WebElement element = waits.clickable(locator);
            element.click();
        } catch (Exception e) {
            WebElement element = safeFind(locator);
            throw failureException(
                    "Failed to click element: " + locator,
                    e,
                    element,
                    "click",
                    locator.toString()
            );
        }
    }

    /**
     * Type text into element with failure context capture.
     */
    public void type(WebElement element, String text) {
        autoLog("Type", element, text);
        try {
            WebElement visible = waits.visibilityOf(element);
            visible.clear();
            visible.sendKeys(text);
        } catch (Exception e) {
            throw failureException("Failed to type text: " + maskIfSensitive(element, text), e, element, "type");
        }
    }

    /**
     * Type text by locator with failure context capture.
     */
    public void type(By locator, String text) {
        autoLog("Type", locator, text);
        try {
            WebElement visible = waits.visibilityOf(locator);
            visible.clear();
            visible.sendKeys(text);
        } catch (Exception e) {
            WebElement element = safeFind(locator);
            throw failureException(
                    "Failed to type text: " + maskIfSensitive(locator, text) + " in " + locator,
                    e,
                    element,
                    "type",
                    locator.toString()
            );
        }
    }

    public void pressKey(WebElement element, Keys key) {
        try {
            waits.visibilityOf(element).sendKeys(key);
        } catch (Exception e) {
            throw failureException("Failed to press key: " + key, e, element, "pressKey(" + key + ")");
        }
    }

    public void pressKey(By locator, Keys key) {
        try {
            WebElement element = waits.visibilityOf(locator);
            element.sendKeys(key);
        } catch (Exception e) {
            WebElement element = safeFind(locator);
            throw failureException(
                    "Failed to press key: " + key + " in " + locator,
                    e,
                    element,
                    "pressKey(" + key + ")",
                    locator.toString()
            );
        }
    }

    public void submit(WebElement element) {
        autoLog("Submit", element);
        try {
            waits.visibilityOf(element).submit();
        } catch (Exception e) {
            throw failureException("Failed to submit form", e, element, "submit");
        }
    }

    public void submit(By locator) {
        try {
            WebElement element = waits.visibilityOf(locator);
            element.submit();
        } catch (Exception e) {
            WebElement element = safeFind(locator);
            throw failureException(
                    "Failed to submit form in " + locator,
                    e,
                    element,
                    "submit",
                    locator.toString()
            );
        }
    }

    // ---------- Reads ----------
    public String getText(WebElement element) {
        try {
            return waits.visibilityOf(element).getText().trim();
        } catch (Exception e) {
            throw failureException("Failed to get text", e, element, "getText");
        }
    }

    public String getAttribute(WebElement element, String attribute) {
        try {
            return waits.visibilityOf(element).getAttribute(attribute);
        } catch (Exception e) {
            throw failureException(
                    "Failed to get attribute: " + attribute, e, element,
                    "getAttribute(" + attribute + ")"
            );
        }
    }

    public String getValue(WebElement element) {
        try {
            return waits.visibilityOf(element).getAttribute("value");
        } catch (Exception e) {
            throw failureException("Failed to get value", e, element, "getValue");
        }
    }

    /**
     * Returns true if the element is visible within the configured wait time.
     */
    public boolean isDisplayed(WebElement element) {
        try {
            return waits.visibilityOf(element).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isDisplayed(By locator) {
        try {
            return waits.visibilityOf(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isEnabled(WebElement element) {
        try {
            return waits.visibilityOf(element).isEnabled();
        } catch (Exception e) {
            throw failureException("Failed to check if element is enabled", e, element, "isEnabled");
        }
    }

    public boolean isSelected(WebElement element) {
        return waits.visibilityOf(element).isSelected();
    }

    // ---------- Dropdowns (<select>) ----------
    public void selectByVisibleText(WebElement element, String text) {
        autoLog("Select (text)", element, text);
        try {
            new Select(waits.visibilityOf(element)).selectByVisibleText(text);
        } catch (Exception e) {
            throw failureException(
                    "Failed to select dropdown option by visible text: "
                            + text, e, element,
                    "selectByVisibleText(" + text + ")"
            );
        }
    }

    public void selectByValue(WebElement element, String value) {
        autoLog("Select (value)", element, value);
        try {
            new Select(waits.visibilityOf(element)).selectByValue(value);
        } catch (Exception e) {
            throw failureException(
                    "Failed to select dropdown option by value: "
                            + value, e, element,
                    "selectByValue(" + value + ")"
            );
        }
    }

    public void selectByIndex(WebElement element, int index) {
        try {
            new Select(waits.visibilityOf(element)).selectByIndex(index);
        } catch (Exception e) {
            throw failureException(
                    "Failed to select dropdown option by index: "
                            + index, e, element,
                    "selectByIndex(" + index + ")"
            );
        }
    }

    public String getSelectedOptionText(WebElement element) {
       try {
           return new Select(waits.visibilityOf(element)).getFirstSelectedOption().getText().trim();
       } catch (Exception e) {
           throw failureException("Failed to get selected dropdown option text", e, element, "getSelectedOptionText");
       }
    }

    public List<WebElement> getAllOptions(WebElement element) {
        try {
            return new Select(waits.visibilityOf(element)).getOptions();
        } catch (Exception e) {
            throw failureException("Failed to get all dropdown options", e, element, "getAllOptions");
        }
    }

    // ---------- Mouse / keyboard chains ----------
    public void hover(WebElement element) {
        autoLog("Hover", element);
        try {
            seleniumActions.moveToElement(waits.visibilityOf(element)).perform();
        } catch (Exception e) {
            throw failureException("Failed to hover over element", e, element, "hover");
        }
    }

    public void doubleClick(WebElement element) {
        autoLog("Double-click", element);
        try {
            seleniumActions.doubleClick(waits.clickable(element)).perform();
        } catch (Exception e) {
            throw failureException("Failed to double-click element", e, element, "doubleClick");
        }
    }

    public void rightClick(WebElement element) {
        try {
            seleniumActions.contextClick(waits.clickable(element)).perform();
        } catch (Exception e) {
            throw failureException("Failed to right-click element", e, element, "rightClick");
        }
    }

    public void dragAndDrop(WebElement source, WebElement target) {
        try {
            seleniumActions.dragAndDrop(waits.visibilityOf(source), waits.visibilityOf(target)).perform();
        } catch (Exception e) {
            throw failureException(
                    "Failed to drag and drop element",
                    e,
                    source,
                    "dragAndDrop(source, target)"
            );
        }
    }

    // ---------- JavaScript helpers ----------
    public void executeJs(String script, Object... args) {
        try {
            ((JavascriptExecutor) driver).executeScript(script, args);
        } catch (Exception e) {
            throw failureException("Failed to execute JavaScript: " + script, e, null, "executeJs");
        }
    }

    public void jsClick(WebElement element) {
        autoLog("JS click", element);
        try {
            executeJs("arguments[0].click();", waits.visibilityOf(element));
        } catch (Exception e) {
            throw failureException("Failed to click element with JavaScript", e, element, "jsClick");
        }
    }

    public void scrollIntoView(WebElement element) {
        try {
            executeJs("arguments[0].scrollIntoView(true);", waits.visibilityOf(element));
        } catch (Exception e) {
            throw failureException("Failed to scroll element into view", e, element, "scrollIntoView");
        }
    }

    public void scrollToTop() {
        try {
            executeJs("window.scrollTo(0, 0);");
        } catch (Exception e) {
            throw failureException("Failed to scroll to top", e, null, "scrollToTop");
        }
    }

    public void scrollToBottom() {
        try {
            executeJs("window.scrollTo(0, document.body.scrollHeight);");
        } catch (Exception e) {
            throw failureException("Failed to scroll to bottom", e, null, "scrollToBottom");
        }
    }

    // ---------- Alerts ----------
    public String getAlertText() {
        try {
            return waits.alertIsPresent().getText();
        } catch (Exception e) {
            throw failureException("Failed to get alert text", e, null, "getAlertText");
        }
    }

    public void acceptAlert() {
        try {
            waits.alertIsPresent().accept();
        } catch (Exception e) {
            throw failureException("Failed to accept alert", e, null, "acceptAlert");
        }
    }

    public void dismissAlert() {
        try {
            waits.alertIsPresent().dismiss();
        } catch (Exception e) {
            throw failureException("Failed to dismiss alert", e, null, "dismissAlert");
        }
    }

    public void typeInAlert(String text) {
        try {
            Alert alert = waits.alertIsPresent();
            alert.sendKeys(text);
        } catch (Exception e) {
            throw failureException("Failed to type in alert", e, null, "typeInAlert");
        }
    }

    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    // ---------- Frames ----------
    public void switchToFrame(WebElement frameElement) {
        try {
            driver.switchTo().frame(waits.visibilityOf(frameElement));
        } catch (Exception e) {
            throw failureException("Failed to switch to frame", e, frameElement, "switchToFrame");
        }
    }

    public void switchToFrame(int indexOrName) {
        try {
            driver.switchTo().frame(indexOrName);
        } catch (Exception e) {
            throw failureException("Failed to switch to frame", e, null, "switchToFrame");
        }
    }

    public void switchToParentFrame() {
        try {
            driver.switchTo().parentFrame();
        } catch (Exception e) {
            throw failureException("Failed to switch to parent frame", e, null, "switchToParentFrame");
        }
    }

    public void switchToDefaultContent() {
        try {
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            throw failureException("Failed to switch to default content", e, null, "switchToDefaultContent");
        }
    }

    //---------- Windows ----------
    public String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }

    public void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    public void closeCurrentWindow() {
        driver.close();
    }

    public void openNewTab(String url) {
        try {
            driver.switchTo().newWindow(WindowType.TAB);
        } catch (Exception e) {
            throw failureException("Failed to open new tab with URL: " + url, e, null, "openNewTab");
        }
    }

    public void openNewWindow(String url) {
        try {
            driver.switchTo().newWindow(WindowType.WINDOW);
        } catch (Exception e) {
            throw failureException("Failed to open new window with URL: " + url, e, null, "openNewWindow");
        }
    }
}
