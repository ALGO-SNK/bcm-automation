package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private final int maxRetries = ConfigReader.getInt("retry.count", 0);
    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < maxRetries) {
            attempt++;
            return true;
        }
        return false;
    }
}
