package com.brcm.utils;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * Captures detailed context about a failed element interaction.
 * Used for enhanced logging when tests fail.
 *
 * Captures:
 * - Element locator (how it was found)
 * - Element visibility and clickability status
 * - Element coordinates on screen
 * - Element size
 * - Element text content
 */
public class FailureContextCapture {

    // Getters for use in ScreenshotAnnotator
    private final String locator;
    private final boolean isDisplayed;
    private final boolean isEnabled;
    private final Point location;
    private final int width;
    private final int height;
    private final String elementText;
    private final String tagName;
    private final String elementId;
    private final String elementClass;
    private final String action;

    private FailureContextCapture(Builder builder) {
        this.locator = builder.locator;
        this.isDisplayed = builder.isDisplayed;
        this.isEnabled = builder.isEnabled;
        this.location = builder.location;
        this.width = builder.width;
        this.height = builder.height;
        this.elementText = builder.elementText;
        this.tagName = builder.tagName;
        this.elementId = builder.elementId;
        this.elementClass = builder.elementClass;
        this.action = builder.action;
    }

    public String getLocator() {
        return locator;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Point getLocation() {
        return location;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getElementText() {
        return elementText;
    }

    public String getTagName() {
        return tagName;
    }

    public String getAction() {
        return action;
    }

    /**
     * Build failure context from a WebElement.
     * Example:
     *   FailureContextCapture ctx = FailureContextCapture.from(element)
     *       .withLocator("By.id('submitButton')")
     *       .withAction("click")
     *       .build();
     */
    public static Builder from(WebElement element) {
        return new Builder(element);
    }

    /**
     * Format all captured context as readable log string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("═".repeat(70)).append("\n");
        sb.append("FAILURE CONTEXT\n");
        sb.append("═".repeat(70)).append("\n");

        if (action != null) {
            sb.append("ACTION: ").append(action).append("\n");
        }

        if (locator != null) {
            sb.append("LOCATOR: ").append(locator).append("\n");
        }

        sb.append("ELEMENT STATUS:\n");
        sb.append("  - Tag: ").append(tagName).append("\n");
        sb.append("  - ID: ").append(nullToNA(elementId)).append("\n");
        sb.append("  - Class: ").append(nullToNA(elementClass)).append("\n");
        sb.append("  - Text: ").append(nullToNA(elementText)).append("\n");

        sb.append("STATE:\n");
        sb.append("  - Displayed: ").append(isDisplayed).append("\n");
        sb.append("  - Enabled: ").append(isEnabled).append("\n");

        sb.append("LOCATION:\n");
        if (location != null) {
            sb.append("  - X: ").append(location.getX()).append("\n");
            sb.append("  - Y: ").append(location.getY()).append("\n");
        } else {
            sb.append("  - X: N/A\n");
            sb.append("  - Y: N/A\n");
        }

        sb.append("SIZE:\n");
        sb.append("  - Width: ").append(width).append("px\n");
        sb.append("  - Height: ").append(height).append("px\n");

        sb.append("═".repeat(70)).append("\n");
        return sb.toString();
    }

    private static String nullToNA(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }

    /**
     * Builder for FailureContextCapture.
     */
    public static class Builder {
        private final WebElement element;
        private String locator;
        private boolean isDisplayed;
        private boolean isEnabled;
        private Point location;
        private int width;
        private int height;
        private String elementText;
        private String tagName;
        private String elementId;
        private String elementClass;
        private String action;

        private Builder(WebElement element) {
            this.element = element;
            captureElementInfo();
        }

        private void captureElementInfo() {
            try {
                this.tagName = element.getTagName();
                this.elementId = element.getAttribute("id");
                this.elementClass = element.getAttribute("class");
                this.elementText = element.getText();
                this.isDisplayed = element.isDisplayed();
                this.isEnabled = element.isEnabled();
                this.location = element.getLocation();
                this.width = element.getSize().getWidth();
                this.height = element.getSize().getHeight();
            } catch (Exception e) {
                // Stale element or other issue - that's actually useful info
            }
        }

        public Builder withLocator(String locator) {
            this.locator = locator;
            return this;
        }

        public Builder withAction(String action) {
            this.action = action;
            return this;
        }

        public FailureContextCapture build() {
            return new FailureContextCapture(this);
        }
    }
}
