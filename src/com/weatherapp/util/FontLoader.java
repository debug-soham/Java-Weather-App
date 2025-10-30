package com.weatherapp.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

/**
 * A utility class to load custom fonts from the project's resources.
 * This ensures the application has a consistent look and feel on any system,
 * regardless of which fonts are installed locally.
 */
public class FontLoader {

    /**
     * Loads a font from a given resource path and derives it with a specified size.
     * @param resourcePath The path to the font file within the resources folder (e.g., "/fonts/Montserrat-Regular.ttf").
     * @param size The desired font size.
     * @return The loaded Font object, or a default "Dialog" font if loading fails.
     */
    public static Font loadFont(String resourcePath, float size) {
        try {
            // Get the font file as a stream from the application's resources.
            InputStream fontStream = FontLoader.class.getResourceAsStream(resourcePath);
            if (fontStream == null) {
                // This error is critical for debugging if font files are misplaced.
                System.err.println("Font not found at path: " + resourcePath);
                return new Font("Dialog", Font.PLAIN, (int) size);
            }
            // Create the font from the stream and derive it to the desired size.
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont); // Register the font to make it available to the system.
            return customFont;
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading font: " + e.getMessage());
            // Fallback to a default system font to prevent the application from crashing.
            return new Font("Dialog", Font.PLAIN, (int) size);
        }
    }
}
