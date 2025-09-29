package com.weatherapp.gui;

import com.weatherapp.api.WeatherApiClient;
import com.weatherapp.model.WeatherData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Represents the main Graphical User Interface for the Weather App.
 * This class sets up the main frame and all the UI components,
 * and handles the event listening for user interactions.
 */
public class WeatherAppGui extends JFrame {

    // Define colors for the dark theme, inspired by the reference UI
    private static final Color BACKGROUND_COLOR = new Color(0x1F1F1F);
    private static final Color COMPONENT_COLOR = new Color(0x2B2B2B);
    private static final Color TEXT_COLOR = new Color(0xE0E0E0);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 16);

    // Weather client for API calls
    private final WeatherApiClient weatherApiClient;

    // UI components that need to be updated with weather data
    private JLabel cityLabel;
    private JLabel tempLabel;
    private JLabel weatherIcon;
    private JLabel windValueLabel;
    private JLabel humidityValueLabel;
    private JLabel visibilityValueLabel;

    public WeatherAppGui() {
        super("Weather Information App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        weatherApiClient = new WeatherApiClient();

        createUI();
    }

    private void createUI() {
        // --- Header Panel with Search ---
        JTextField searchField = new JTextField();
        searchField.setFont(MAIN_FONT);
        searchField.setForeground(TEXT_COLOR);
        searchField.setBackground(COMPONENT_COLOR);
        searchField.setCaretColor(TEXT_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x4a4a4a)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.add(searchField, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Panel ---
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        add(mainContentPanel, BorderLayout.CENTER);

        // --- Today's Weather Panel ---
        JPanel todayPanel = new JPanel(new BorderLayout());
        todayPanel.setBackground(COMPONENT_COLOR);
        todayPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cityLabel = new JLabel("Enter a City");
        cityLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        cityLabel.setForeground(TEXT_COLOR);

        tempLabel = new JLabel("--°C");
        tempLabel.setFont(new Font("Segoe UI", Font.BOLD, 64));
        tempLabel.setForeground(TEXT_COLOR);

        weatherIcon = new JLabel();

        JPanel tempPanel = new JPanel();
        tempPanel.setBackground(COMPONENT_COLOR);
        tempPanel.add(tempLabel);
        tempPanel.add(weatherIcon);

        todayPanel.add(cityLabel, BorderLayout.NORTH);
        todayPanel.add(tempPanel, BorderLayout.CENTER);

        mainContentPanel.add(todayPanel, BorderLayout.NORTH);

        // --- Overview Panel ---
        // Changed GridLayout to have 3 columns instead of 4
        JPanel overviewContainer = new JPanel(new GridLayout(1, 3, 15, 0));
        overviewContainer.setBackground(BACKGROUND_COLOR);
        overviewContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(20, 0, 0, 0),
                "Today's Overview",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                BOLD_FONT, TEXT_COLOR
        ));

        windValueLabel = new JLabel("-- km/h");
        humidityValueLabel = new JLabel("--%");
        visibilityValueLabel = new JLabel("-- km");

        overviewContainer.add(createDetailPanel("Wind Status", windValueLabel));
        overviewContainer.add(createDetailPanel("Humidity", humidityValueLabel));
        overviewContainer.add(createDetailPanel("Visibility", visibilityValueLabel));

        mainContentPanel.add(overviewContainer, BorderLayout.CENTER);

        // --- Add Action Listener to the Search Field ---
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = searchField.getText();
                fetchWeatherData(city);
            }
        });
    }

    private JPanel createDetailPanel(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COMPONENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(MAIN_FONT);
        titleLabel.setForeground(TEXT_COLOR.darker());

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private void fetchWeatherData(String city) {
        cityLabel.setText("Loading...");
        weatherIcon.setIcon(null);

        SwingWorker<WeatherData, Void> worker = new SwingWorker<>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                return weatherApiClient.getWeatherData(city);
            }

            @Override
            protected void done() {
                try {
                    WeatherData data = get();
                    if (data != null) {
                        updateUI(city, data);
                    } else {
                        cityLabel.setText("City not found");
                        tempLabel.setText("--°C");
                        windValueLabel.setText("-- km/h");
                        humidityValueLabel.setText("--%");
                        visibilityValueLabel.setText("-- km");
                        weatherIcon.setIcon(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    cityLabel.setText("Error fetching data");
                }
            }
        };
        worker.execute();
    }

    private void updateUI(String city, WeatherData data) {
        cityLabel.setText(city.substring(0, 1).toUpperCase() + city.substring(1));
        tempLabel.setText(String.format("%.0f°C", data.getTemperature()));
        windValueLabel.setText(String.format("%.2f km/h", data.getWindSpeed()));
        humidityValueLabel.setText(data.getHumidity() + "%");
        visibilityValueLabel.setText((data.getVisibility() / 1000) + " km");

        ImageIcon icon = loadWeatherIcon(data.getIconCode());
        if (icon != null) {
            weatherIcon.setIcon(icon);
        }
    }

    private ImageIcon loadWeatherIcon(String iconCode) {
        String path = "/assets/" + iconCode + ".png";
        try {
            URL resourceUrl = getClass().getResource(path);
            if (resourceUrl == null) {
                System.err.println("Could not find icon file: " + path);
                return null;
            }
            return new ImageIcon(resourceUrl);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path);
            e.printStackTrace();
            return null;
        }
    }
}

