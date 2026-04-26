package com.brcm.utils;

import com.aventstack.extentreports.ExtentTest;
import com.brcm.core.DriverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;

public class TestListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        LOG.info("Starting test suite: {}", context.getName());
        ExtentManager.getReporter();
    }

    @Override
    public void onFinish(ITestContext context) {
        LOG.info("Finished test suite: {}", context.getName());
        ExtentManager.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String parameterSuffix = buildParameterSuffix(result);
        String testName = result.getMethod().getMethodName() + parameterSuffix;
        LOG.info("Starting test: {}.{}", result.getTestClass().getName(), testName);
        ExtentManager.createTest(testName)
                .info("Starting test: " + result.getTestClass().getName() + "." + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("Test passed: {}.{}", result.getTestClass().getName(), result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.pass("Test passed");
            if (ConfigReader.getBoolean("screenshot.on.pass", false)) {
                attachScreenshot(result, test);
            }
        }
        ExtentManager.unloadTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("Test failed: {}.{}", result.getTestClass().getName(), result.getMethod().getMethodName(),
                result.getThrowable());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                test.fail(throwable);
            } else {
                test.fail("Test failed without exception details");
            }

            // Log detailed failure context if available
            FailureContextCapture failureContext = extractFailureContext(result);
            if (failureContext != null) {
                test.info(failureContext.toString());
            }

            attachScreenshot(result, test, failureContext);
        }
        ExtentManager.unloadTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("Test skipped: {}.{}", result.getTestClass().getName(), result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                test.skip(throwable);
            } else {
                test.skip("Test skipped");
            }
        }
        ExtentManager.unloadTest();
    }

    private void attachScreenshot(ITestResult result, ExtentTest test, FailureContextCapture failureContext) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            return;
        }

        String testName = result.getMethod().getMethodName();
        String screenshotPath;

        // Use annotated screenshot if we have failure context
        if (failureContext != null) {
            screenshotPath = ScreenshotUtils.captureWithAnnotation(driver, testName, failureContext);
        } else {
            screenshotPath = ScreenshotUtils.capture(driver, testName);
        }

        if (screenshotPath != null) {
            test.addScreenCaptureFromPath(toReportRelativePath(screenshotPath));
        }
    }

    private void attachScreenshot(ITestResult result, ExtentTest test) {
        attachScreenshot(result, test, null);
    }

    private String buildParameterSuffix(ITestResult result) {
        Object[] parameters = result.getParameters();
        if (parameters == null || parameters.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder(" [");
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(String.valueOf(parameters[i]));
        }
        builder.append(']');
        return builder.toString();
    }

    /**
     * Extracts failure context from exception if available.
     * Looks for FailureContextCapture attached to the exception.
     */
    private FailureContextCapture extractFailureContext(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable == null) {
            return null;
        }

        // Look for FailureContextCapture in exception chain
        Throwable cause = throwable;
        while (cause != null) {
            if (cause instanceof FailureContextException) {
                return ((FailureContextException) cause).getFailureContext();
            }
            cause = cause.getCause();
        }
        return null;
    }

    private static String toReportRelativePath(String screenshotPath) {
        Path reportDir = Path.of("test-output", "extent").toAbsolutePath();
        Path screenshotFile = Path.of(screenshotPath).toAbsolutePath();
        return reportDir.relativize(screenshotFile).toString().replace('\\', '/');
    }
}
