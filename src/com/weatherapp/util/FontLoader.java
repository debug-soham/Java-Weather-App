package com.weatherapp.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * A utility class to load and register custom fonts from the project's resources.
 * This ensures that the custom font is available to the entire Swing application.
 */
public class FontLoader {

    /**
     * Loads and registers the Montserrat font (Regular and Bold styles).
     * If the font fails to load, it prints an error message but does not crash the app,
     * allowing the UI to fall back to a default system font.
     */
    public static void loadFonts() {
        try {
            // Load the regular and bold font files from the resources/fonts folder
            InputStream regularStream = FontLoader.class.getResourceAsStream("/fonts/Montserrat-Regular.ttf");
            InputStream boldStream = FontLoader.class.getResourceAsStream("/fonts/Montserrat-Bold.ttf");

            if (regularStream != null && boldStream != null) {
                Font montserratRegular = Font.createFont(Font.TRUETYPE_FONT, regularStream);
                Font montserratBold = Font.createFont(Font.TRUETYPE_FONT, boldStream);

                // Register the fonts with the system's GraphicsEnvironment
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(montserratRegular);
                ge.registerFont(montserratBold);
            } else {
                System.err.println("Could not find font files in resources.");
            }
        } catch (Exception e) {
            System.err.println("Error loading custom fonts.");
            e.printStackTrace();
        }
    }
}
