package com.brcm.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots from browser with optional failure annotation.
 *
 * Usage:
 *   // Basic screenshot
 *   String path = ScreenshotUtils.capture(driver, "testName");
 *
 *   // With failure annotation (red box + text label on failed element)
 *   FailureContextCapture ctx = FailureContextCapture.from(element)
 *       .withAction("click")
 *       .withLocator("By.id('submit')")
 *       .build();
 *   String path = ScreenshotUtils.captureWithAnnotation(driver, "testName", ctx);
 */
public final class ScreenshotUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {}

    /**
     * Capture plain screenshot (no annotations).
     */
    public static String capture(WebDriver driver, String testName) {
        if (driver == null) {
            return null;
        }
        try {
            Path screenshotDir = Path.of("test-output", "screenshots");
            Files.createDirectories(screenshotDir);
            String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = safeName + "_" + LocalDateTime.now().format(FORMATTER) + ".png";
            Path destination = screenshotDir.resolve(fileName);

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to capture screenshot", e);
        }
    }

    /**
     * Capture screenshot and annotate with failure context.
     * Draws red rectangle around failed element with text label.
     *
     * @param driver WebDriver instance
     * @param testName test method name
     * @param failureContext element failure details
     * @return path to annotated screenshot
     */
    public static String captureWithAnnotation(WebDriver driver, String testName, FailureContextCapture failureContext) {
        String screenshotPath = capture(driver, testName);
        if (screenshotPath != null && failureContext != null) {
            ScreenshotAnnotator.annotate(screenshotPath, failureContext);
        }
        return screenshotPath;
    }
}
