package tests.api;

import core.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.util.HashMap;
import java.util.Map;

public class UserAPITest extends BaseTest {

    @Override
    protected boolean requiresUI() {
        return false;
    }

    @Test(groups = {"api", "smoke"})
    public void getUserShouldReturn200() {
        requireConfigValue("api.base.url");
        String endpoint = ConfigReader.getOrDefault("api.user.endpoint", "/users/1");
        Response response = apiClient.get(endpoint);

        Assert.assertEquals(response.statusCode(), 200, "GET user API status code mismatch.");
        Assert.assertNotNull(response.asString(), "Response body should not be null.");
    }

    @Test(groups = {"api"})
    public void createUserShouldReturn201Or200() {
        requireConfigValue("api.base.url");
        String endpoint = ConfigReader.getOrDefault("api.create.user.endpoint", "/users");

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Automation User");
        payload.put("job", "SDET");

        Response response = apiClient.post(endpoint, payload);
        int status = response.statusCode();
        Assert.assertTrue(status == 200 || status == 201, "POST user API status code mismatch.");
    }
}
