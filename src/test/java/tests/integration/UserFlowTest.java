package tests.integration;

import core.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.ConfigReader;

public class UserFlowTest extends BaseTest {

    @Test(groups = {"integration"})
    public void userApiAndUiAvailabilityFlow() {
        requireConfigValue("api.base.url");
        requireConfigValue("ui.base.url");

        String endpoint = ConfigReader.getOrDefault("api.user.endpoint", "/users/1");
        Response response = apiClient.get(endpoint);
        Assert.assertEquals(response.statusCode(), 200, "Precondition API call failed.");

        LoginPage loginPage = new LoginPage();
        loginPage.open(ConfigReader.get("ui.base.url"));
        Assert.assertTrue(loginPage.isLoaded(), "UI precondition failed. Login page not loaded.");
    }
}
