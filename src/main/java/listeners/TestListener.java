package listeners;

import com.aventstack.extentreports.ExtentTest;
import core.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import reports.ExtentManager;
import utils.ConfigReader;
import utils.ScreenshotUtils;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getReporter();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentManager.createTest(testName)
                .info("Starting test: " + result.getTestClass().getName() + "." + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
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
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                test.fail(throwable);
            } else {
                test.fail("Test failed without exception details");
            }
            attachScreenshot(result, test);
        }
        ExtentManager.unloadTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
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

    private void attachScreenshot(ITestResult result, ExtentTest test) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            return;
        }
        String screenshotPath = ScreenshotUtils.capture(driver, result.getMethod().getMethodName());
        if (screenshotPath != null) {
            test.addScreenCaptureFromPath(screenshotPath);
        }
    }
}
