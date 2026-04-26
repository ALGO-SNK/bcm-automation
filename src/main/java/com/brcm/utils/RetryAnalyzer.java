package com.brcm.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Retries a failed test up to retry.count (set in config.properties).
 *
 * Beginner usage:
 *   &#064;Test(retryAnalyzer = RetryAnalyzer.class)
 *   public void myTest() { ... }
 *
 * NOTE: TestNG creates a new analyzer per @Test method, but reuses it across
 * DataProvider rows. We keep a per-method-per-parameter counter so retry state
 * is correct in both parallel and data-driven runs.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final ConcurrentHashMap<String, AtomicInteger> ATTEMPTS = new ConcurrentHashMap<>();
    private final int maxRetries = ConfigReader.getInt("retry.count", 0);

    @Override
    public boolean retry(ITestResult result) {
        String key = methodKey(result);
        int attempts = ATTEMPTS.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();
        if (attempts <= maxRetries) {
            return true;
        }
        ATTEMPTS.remove(key);
        return false;
    }

    private String methodKey(ITestResult result) {
        StringBuilder key = new StringBuilder(result.getMethod().getQualifiedName());
        Object[] params = result.getParameters();
        if (params != null) {
            for (Object p : params) {
                key.append('|').append(p);
            }
        }
        return key.toString();
    }
}
