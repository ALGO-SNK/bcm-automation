package com.brcm.utils;

/**
 * Exception that carries failure context information.
 *
 * Wraps the original exception and attaches element failure details.
 * Used to pass context from UIActions to TestListener for logging and annotation.
 *
 * Usage:
 *   try {
 *       element.click();
 *   } catch (Exception e) {
 *       FailureContextCapture ctx = FailureContextCapture.from(element)
 *           .withAction("click")
 *           .withLocator(locator)
 *           .build();
 *       throw new FailureContextException(e, ctx);
 *   }
 */
public class FailureContextException extends RuntimeException {

    private final FailureContextCapture failureContext;

    public FailureContextException(String message, FailureContextCapture failureContext) {
        super(message);
        this.failureContext = failureContext;
    }

    public FailureContextException(Throwable cause, FailureContextCapture failureContext) {
        super(cause);
        this.failureContext = failureContext;
    }

    public FailureContextException(String message, Throwable cause, FailureContextCapture failureContext) {
        super(message, cause);
        this.failureContext = failureContext;
    }

    public FailureContextCapture getFailureContext() {
        return failureContext;
    }

}
