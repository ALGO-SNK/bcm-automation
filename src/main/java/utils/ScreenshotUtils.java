package utils;

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

public final class ScreenshotUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
    }

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
}
