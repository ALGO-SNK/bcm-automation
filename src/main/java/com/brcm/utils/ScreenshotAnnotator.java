package com.brcm.utils;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Annotates screenshots with visual markers for failed elements.
 *
 * Draws:
 * - Red rectangle around failed element
 * - Text label showing action and locator
 * - Coordinates and size information
 *
 * Usage:
 *   ScreenshotAnnotator.annotate(screenshotPath, failureContext);
 */
public final class ScreenshotAnnotator {

    private static final int BORDER_WIDTH = 3;
    private static final Color HIGHLIGHT_COLOR = Color.RED;
    private static final Color TEXT_BG_COLOR = new Color(255, 0, 0, 200);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);

    private ScreenshotAnnotator() {}

    /**
     * Annotates a screenshot with failure context.
     *
     * Draws a red rectangle around the failed element location and adds
     * text annotation showing the action, locator, and coordinates.
     *
     * @param screenshotPath path to screenshot file
     * @param context failure context with element location/size
     * @return true if annotation succeeded, false otherwise
     */
    public static boolean annotate(String screenshotPath, FailureContextCapture context) {
        if (screenshotPath == null || context == null) {
            return false;
        }

        try {
            File screenshotFile = new File(screenshotPath);
            if (!screenshotFile.exists()) {
                return false;
            }

            BufferedImage image = ImageIO.read(screenshotFile);
            if (image == null) {
                return false;
            }

            Graphics2D g2d = image.createGraphics();
            try {
                enableAntialiasing(g2d);

                // Draw element highlight (red rectangle)
                if (context.getLocation() != null && context.getWidth() > 0 && context.getHeight() > 0) {
                    drawElementHighlight(g2d, context);
                }

                // Draw text label
                drawLabel(g2d, context);

            } finally {
                g2d.dispose();
            }

            // Save annotated image
            ImageIO.write(image, "png", screenshotFile);
            return true;

        } catch (IOException e) {
            // Silently fail - don't break test on annotation error
            return false;
        }
    }

    /**
     * Draws a red rectangle around the failed element.
     */
    private static void drawElementHighlight(Graphics2D g2d, FailureContextCapture context) {
        int x = context.getLocation().getX();
        int y = context.getLocation().getY();
        int width = context.getWidth();
        int height = context.getHeight();

        // Draw outer red border
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.setStroke(new BasicStroke(BORDER_WIDTH));
        g2d.drawRect(x, y, width, height);

        // Draw corner markers for better visibility
        int cornerSize = 10;
        drawCorner(g2d, x, y, cornerSize, true);
        drawCorner(g2d, x + width - cornerSize, y, cornerSize, false);
        drawCorner(g2d, x, y + height - cornerSize, cornerSize, false);
        drawCorner(g2d, x + width - cornerSize, y + height - cornerSize, cornerSize, false);
    }

    /**
     * Draws corner markers at element corners.
     */
    private static void drawCorner(Graphics2D g2d, int x, int y, int size, boolean topLeft) {
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.setStroke(new BasicStroke(BORDER_WIDTH + 2));

        if (topLeft) {
            g2d.drawLine(x, y, x + size, y);
            g2d.drawLine(x, y, x, y + size);
        } else {
            g2d.drawLine(x, y, x + size, y);
            g2d.drawLine(x + size, y, x + size, y + size);
        }
    }

    /**
     * Draws text label with action, locator, and coordinates.
     */
    private static void drawLabel(Graphics2D g2d, FailureContextCapture context) {
        StringBuilder labelText = new StringBuilder();

        if (context.getAction() != null) {
            labelText.append("✗ ").append(context.getAction().toUpperCase());
        } else {
            labelText.append("✗ FAILED");
        }

        if (context.getLocator() != null) {
            labelText.append(" | ").append(context.getLocator());
        }

        // Add coordinates if available
        if (context.getLocation() != null) {
            labelText.append(" @ (").append(context.getLocation().getX())
                    .append(", ").append(context.getLocation().getY()).append(")");
        }

        drawTextBox(g2d, labelText.toString(), 10, 20);
    }

    /**
     * Draws a text box with semi-transparent background.
     */
    private static void drawTextBox(Graphics2D g2d, String text, int x, int y) {
        g2d.setFont(LABEL_FONT);
        var metrics = g2d.getFontMetrics(LABEL_FONT);
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        int padding = 8;

        // Draw background rectangle
        g2d.setColor(TEXT_BG_COLOR);
        g2d.fillRect(x - padding, y - textHeight + padding, textWidth + (padding * 2), textHeight + padding);

        // Draw text
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(text, x, y);
    }

    /**
     * Enable antialiasing for smoother lines and text.
     */
    private static void enableAntialiasing(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
