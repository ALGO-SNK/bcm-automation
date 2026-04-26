package com.brcm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define class-level default configuration for tests.
 *
 * Allows each test class to specify its default environment settings.
 * These defaults are overridden by suite parameters (testng.xml) if provided.
 *
 * Priority order:
 *   1. Suite parameters (testng.xml) - HIGHEST
 *   2. Class-level @TestDefaults annotation
 *   3. Global config.properties - LOWEST
 *
 * Usage:
 *   @TestConfig(
 *       baseUrl = "https://mis-teams-team5.bromcom.dev/",
 *       browser = "firefox",
 *       resolution = "1920x1080"
 *   )
 *   public class Team5Tests extends BaseTest {
 *       @Test
 *       public void testFeature() { ... }
 *   }
 *
 * Running locally uses class defaults:
 *   mvn test -Dtest=Team5Tests
 *   → baseUrl: https://mis-teams-team5.bromcom.dev/
 *   → browser: firefox
 *   → resolution: 1920x1080
 *
 * Running with suite overrides class defaults:
 *   testng.xml: <parameter name="BASE_URL" value="https://..."/>
 *   → baseUrl: from testng.xml (suite wins)
 *   → browser: firefox (class default, no suite override)
 *   → resolution: 1920x1080 (class default, no suite override)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestEnvironment {

    /**
     * Overridden by suite parameter BASE_URL if provided.
     */
    String baseUrl() default "";

    /**
     * Default browser for the test class.
     * Valid values: "chrome", "firefox", "edge", "safari"
     *
     * Overridden by suite parameter BROWSER if provided.
     */
    String browser() default "chrome";

    /**
     * Overridden by suite parameter RESOLUTION if provided.
     */
    String resolution() default "FULL_HD";
}
