package core;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiClient {

    private final RequestSpecification baseSpec;
    private final ThreadLocal<String> authToken = new ThreadLocal<>();

    public ApiClient(String baseUri) {
        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .build();
    }

    public ApiClient withToken(String token) {
        authToken.set(token);
        return this;
    }

    public Response get(String path) {
        return request().get(path);
    }

    public Response post(String path, Object body) {
        return request().body(body).post(path);
    }

    public Response put(String path, Object body) {
        return request().body(body).put(path);
    }

    public Response delete(String path) {
        return request().delete(path);
    }

    private RequestSpecification request() {
        RequestSpecification spec = given().spec(baseSpec).relaxedHTTPSValidation();
        String token = authToken.get();
        if (token != null && !token.isBlank()) {
            spec.header("Authorization", "Bearer " + token);
        }
        return spec;
    }
}
