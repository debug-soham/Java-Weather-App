package com.weatherapp.main;

import com.weatherapp.gui.WeatherAppGui;
import javax.swing.SwingUtilities;

/**
 * The entry point for the Weather Information App.
 * This class contains the main method to launch the application.
 */
public class Main {
    public static void main(String[] args) {
        // All Swing GUI updates must be done on the Event Dispatch Thread (EDT)
        // for thread safety. SwingUtilities.invokeLater ensures that the GUI
        // creation and visibility are handled correctly on the EDT.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create and display the main GUI window.
                new WeatherAppGui().setVisible(true);
            }
        });
    }
}
