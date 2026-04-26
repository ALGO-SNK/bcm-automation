package com.brcm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * Auto-logs TestNG method lifecycle (▶ START / ✔ PASS / ✖ FAIL / ⏭ SKIP).
 * Registered automatically via META-INF/services/org.testng.ITestNGListener.
 */
public class MethodInvocationLogger implements IInvokedMethodListener {

    private static final Logger LOG = LoggerFactory.getLogger("TestLog");

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        if (!method.isTestMethod()) return;
        LOG.info("▶ START  {}", result.getMethod().getMethodName());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (!method.isTestMethod()) return;
        String name = result.getMethod().getMethodName();
        long durMs = result.getEndMillis() - result.getStartMillis();
        switch (result.getStatus()) {
            case ITestResult.SUCCESS -> LOG.info("✔ PASS   {} ({} ms)", name, durMs);
            case ITestResult.FAILURE -> LOG.error("✖ FAIL   {} ({} ms) — {}", name, durMs,
                    result.getThrowable() != null ? result.getThrowable().getMessage() : "no details");
            case ITestResult.SKIP    -> LOG.warn("⏭ SKIP   {}", name);
            default -> LOG.info("   END    {} status={}", name, result.getStatus());
        }
    }
}
