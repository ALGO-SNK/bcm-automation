package com.brcm.pages;

import com.brcm.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private static final By HEADER = By.cssSelector("#header");
    private static final By VERTICAL_MENU = By.cssSelector("#verticalmenu");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    /** Returns true if the URL no longer points to the login page. */
    public boolean isLoaded() {
        String url = driver.getCurrentUrl();
        return url != null && !url.toLowerCase().contains("login");
    }

    public boolean isHeaderDisplayed() {
        return actions.isDisplayed(HEADER);
    }

    public boolean isVerticalMenuDisplayed() {
        return actions.isDisplayed(VERTICAL_MENU);
    }

    public boolean areCoreElementsVisible() {
        return isHeaderDisplayed() && isVerticalMenuDisplayed();
    }
}
