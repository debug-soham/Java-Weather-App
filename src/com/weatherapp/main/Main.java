package com.weatherapp.main;

import com.weatherapp.gui.WeatherAppGui;
import javax.swing.SwingUtilities;

/**
 * Main class to run the Weather Information App.
 * This class contains the main method which is the entry point of the application.
 */
public class Main {
    public static void main(String[] args) {
        // The GUI creation and updates should be done on the Event Dispatch Thread (EDT)
        // for thread safety, which is a best practice in Swing applications.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create and display the GUI
                new WeatherAppGui().setVisible(true);
            }
        });
    }
}
