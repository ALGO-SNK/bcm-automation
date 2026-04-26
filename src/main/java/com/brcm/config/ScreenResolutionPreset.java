package com.brcm.config;

import org.openqa.selenium.Dimension;

public enum ScreenResolutionPreset {
    XGA(1024, 768),
    WXGA(1366, 768),
    HD(1280, 720),
    FULL_HD(1920, 1080),
    QHD(2560, 1440),
    ULTRAWIDE(3440, 1440);

    private final int width;
    private final int height;

    ScreenResolutionPreset(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** Direct conversion to Selenium Dimension */
    public Dimension toDimension() {
        return new Dimension(width, height);
    }

    /** Lookup by key */
    public static Dimension getDimension(String key) {
        if (key == null || key.isBlank()) return null;

        try {
            return ScreenResolutionPreset.valueOf(key.toUpperCase()).toDimension();
        } catch (IllegalArgumentException e) {
            return parseRaw(key); // fallback
        }
    }

    /** Supports "1920x1080" */
    private static Dimension parseRaw(String value) {
        String[] parts = value.toLowerCase().split("x");
        if (parts.length != 2) return null;

        try {
            int width = Integer.parseInt(parts[0].trim());
            int height = Integer.parseInt(parts[1].trim());
            return new Dimension(width, height);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
