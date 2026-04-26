package com.brcm.utils;

import com.aventstack.extentreports.ExtentTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized, human-readable logging for tests.
 *
 * All methods write to:
 *   1. SLF4J logger (console + per-run log file in test-output/logs/)
 *   2. Extent Report (if a test is active on this thread)
 *
 * Usage in tests/pages:
 *   LogUtils.step("Navigate to login page");
 *   LogUtils.action("Enter username: admin");
 *   LogUtils.assertion("Dashboard is displayed");
 *   LogUtils.info("Session token acquired");
 *   LogUtils.warn("Slow page load detected");
 *   LogUtils.failure("Login button not clickable", throwable);
 */
public final class LogUtils {

    private static final Logger LOG = LoggerFactory.getLogger("TestLog");

    private LogUtils() {}

    /** High-level test step (e.g. "Login with admin user"). */
    public static void step(String message) {
        LOG.info("[STEP] {}", message);
        attachToExtent("STEP", message, false);
    }

    /** Low-level action (click, type, select). */
    public static void action(String message) {
        LOG.info("[ACTION] {}", message);
        attachToExtent("ACTION", message, false);
    }

    /** Assertion / verification. */
    public static void assertion(String message) {
        LOG.info("[VERIFY] {}", message);
        attachToExtent("VERIFY", message, false);
    }

    /** General informational message. */
    public static void info(String message) {
        LOG.info("[INFO] {}", message);
        attachToExtent("INFO", message, false);
    }

    public static void warn(String message) {
        LOG.warn("[WARN] {}", message);
        attachToExtent("WARN", message, false);
    }

    public static void failure(String message, Throwable t) {
        LOG.error("[FAIL] {}", message, t);
        attachToExtent("FAIL", message + " — " + (t != null ? t.getMessage() : ""), true);
    }

    public static void failure(String message) {
        failure(message, null);
    }

    private static void attachToExtent(String tag, String message, boolean asFailure) {
        ExtentTest test = ExtentManager.getTest();
        if (test == null) return;
        String formatted = "[" + tag + "] " + message;
        if (asFailure) {
            test.fail(formatted);
        } else {
            test.info(formatted);
        }
    }
}
