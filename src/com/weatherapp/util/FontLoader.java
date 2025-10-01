package com.weatherapp.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

/**
 * A utility class to load and register custom fonts from the project's resources.
 */
public class FontLoader {

    /**
     * Loads a custom font from the specified path within the resources.
     * @param path The resource path to the .ttf font file.
     * @return The loaded Font object, or a fallback font if loading fails.
     */
    public static Font loadFont(String path, float size) {
        try {
            // Get the font file as a stream from the resources folder
            InputStream fontStream = FontLoader.class.getResourceAsStream(path);
            if (fontStream == null) {
                System.err.println("Font not found at path: " + path);
                return new Font("Dialog", Font.PLAIN, (int) size);
            }
            // Create the font and register it with the graphics environment
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont;
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading font: " + e.getMessage());
            // Return a default font in case of error
            return new Font("Dialog", Font.PLAIN, (int) size);
        }
    }
}

