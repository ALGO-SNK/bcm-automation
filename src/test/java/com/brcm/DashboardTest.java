package com.brcm;

import com.brcm.base.BaseTest;
import com.brcm.annotation.TestEnvironment;
import com.brcm.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Dashboard checks for visible shell elements after a successful login.
 */
@TestEnvironment(
        baseUrl = "https://mis-release.bromcom.dev/",
        browser = "chrome",
        resolution = "FULL_HD"
)
public class DashboardTest extends BaseTest {

    @Test(groups = {"regression"})
    public void dashboardShouldShowHeaderAndVerticalMenu() {
        DashboardPage dashboard = loginFor("secondary");

        Assert.assertTrue(dashboard.isLoaded(), "Dashboard page did not load.");
        Assert.assertTrue(dashboard.isHeaderDisplayed(), "Dashboard header (#header) should be visible.");
        Assert.assertTrue(dashboard.isVerticalMenuDisplayed(), "Dashboard vertical menu (#verticalmenu) should be visible.");
    }
}
