package com.brcm;

import com.brcm.base.BaseTest;
import com.brcm.annotation.TestEnvironment;
import com.brcm.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Release environment tests on RELEASE server.
 *
 * Class defaults apply locally; suite parameters override them at runtime.
 */
@TestEnvironment(
        baseUrl = "https://mis-release.bromcom.dev/",
        browser = "chrome",
        resolution = "FULL_HD"
)
public class ReleaseTests extends BaseTest {

    @Test(groups = {"regression"})
    public void loginWithSecondarySchoolOnRelease() {
        DashboardPage dashboard = loginFor("secondary");
        Assert.assertTrue(dashboard.isLoaded(),
                "Should login with secondary school on RELEASE environment");
    }

    @Test(groups = {"regression"})
    public void loginWithPrimarySchoolOnRelease() {
        DashboardPage dashboard = loginFor("primary");
        Assert.assertTrue(dashboard.isLoaded(),
                "Should login with primary school on RELEASE environment");
    }
}
