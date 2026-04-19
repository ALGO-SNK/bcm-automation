package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import utils.ConfigReader;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ExtentManager {

    private static final ThreadLocal<ExtentTest> TL_TEST = new ThreadLocal<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static ExtentReports extentReports;

    private ExtentManager() {
    }

    public static synchronized ExtentReports getReporter() {
        if (extentReports == null) {
            String reportName = "extent-report-" + LocalDateTime.now().format(FORMATTER) + ".html";
            Path reportPath = Path.of("test-output", "extent", reportName);
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath.toString());
            sparkReporter.config().setReportName("SK-Vtiger Test Execution Report");
            sparkReporter.config().setDocumentTitle("Automation Results");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Framework", "Selenium + TestNG + RestAssured");
            extentReports.setSystemInfo("Browser", ConfigReader.getOrDefault("browser", "chrome"));
        }
        return extentReports;
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest test = getReporter().createTest(testName);
        TL_TEST.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return TL_TEST.get();
    }

    public static void unloadTest() {
        TL_TEST.remove();
    }

    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
