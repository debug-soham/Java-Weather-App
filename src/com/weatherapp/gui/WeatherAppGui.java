package com.weatherapp.gui;

import com.weatherapp.api.WeatherApiClient;
import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;
import com.weatherapp.util.FontLoader;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Represents the main Graphical User Interface for the Weather App.
 */
public class WeatherAppGui extends JFrame {

    // Define colors and fonts
    private static final Color BACKGROUND_COLOR = new Color(0x1F1F1F);
    private static final Color COMPONENT_COLOR = new Color(0x2B2B2B);
    private static final Color TEXT_COLOR = new Color(0xE0E0E0);
    private static final String FONT_FAMILY = "Montserrat";
    private static final Font MAIN_FONT = new Font(FONT_FAMILY, Font.PLAIN, 16);
    private static final Font BOLD_FONT = new Font(FONT_FAMILY, Font.BOLD, 16);

    private final WeatherApiClient weatherApiClient;

    // UI components for current weather
    private JLabel cityLabel, tempLabel, weatherIcon, weatherDescLabel;
    private JLabel windValueLabel, humidityValueLabel, visibilityValueLabel;

    // UI component for the forecast panel
    private JPanel forecastPanel;

    public WeatherAppGui() {
        super("Weather Information App");
        FontLoader.loadFonts();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 750); // Increased height for forecast panel
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        weatherApiClient = new WeatherApiClient();
        createUI();
    }

    private void createUI() {
        // ... (Header Panel with Search - no changes) ...
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
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        add(mainContentPanel, BorderLayout.CENTER);

        // ... (Today's Weather Panel - no changes) ...
        JPanel todayPanel = new JPanel(new BorderLayout());
        todayPanel.setBackground(COMPONENT_COLOR);
        todayPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cityLabel = new JLabel("Enter a City");
        cityLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));
        cityLabel.setForeground(TEXT_COLOR);
        cityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tempLabel = new JLabel("--°C");
        tempLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 64));
        tempLabel.setForeground(TEXT_COLOR);
        tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
        weatherIcon = new JLabel();
        weatherIcon.setHorizontalAlignment(SwingConstants.CENTER);
        weatherDescLabel = new JLabel(" ");
        weatherDescLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 20));
        weatherDescLabel.setForeground(TEXT_COLOR);
        weatherDescLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel tempIconPanel = new JPanel();
        tempIconPanel.setBackground(COMPONENT_COLOR);
        tempIconPanel.add(tempLabel);
        tempIconPanel.add(weatherIcon);
        JPanel centerContentPanel = new JPanel(new BorderLayout());
        centerContentPanel.setBackground(COMPONENT_COLOR);
        centerContentPanel.add(tempIconPanel, BorderLayout.NORTH);
        centerContentPanel.add(weatherDescLabel, BorderLayout.SOUTH);
        todayPanel.add(cityLabel, BorderLayout.NORTH);
        todayPanel.add(centerContentPanel, BorderLayout.CENTER);

        // ... (Overview Panel - no changes) ...
        JPanel overviewContainer = new JPanel(new GridLayout(1, 3, 15, 0));
        overviewContainer.setBackground(BACKGROUND_COLOR);
        overviewContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(20, 0, 0, 0), "Today's Overview",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                BOLD_FONT, TEXT_COLOR
        ));
        windValueLabel = new JLabel("-- km/h");
        humidityValueLabel = new JLabel("--%");
        visibilityValueLabel = new JLabel("-- km");
        overviewContainer.add(createDetailPanel("Wind Status", windValueLabel));
        overviewContainer.add(createDetailPanel("Humidity", humidityValueLabel));
        overviewContainer.add(createDetailPanel("Visibility", visibilityValueLabel));

        // --- Forecast Panel ---
        forecastPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        forecastPanel.setBackground(BACKGROUND_COLOR);
        forecastPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        mainContentPanel.add(todayPanel);
        mainContentPanel.add(overviewContainer);
        mainContentPanel.add(forecastPanel);

        searchField.addActionListener(e -> fetchWeatherData(searchField.getText()));
    }

    private JPanel createDetailPanel(String title, JLabel valueLabel) {
        // ... (no changes in this method) ...
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COMPONENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(MAIN_FONT);
        titleLabel.setForeground(TEXT_COLOR.darker());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void fetchWeatherData(String city) {
        if (city == null || city.trim().isEmpty()) return;

        // Reset UI to loading state
        cityLabel.setText("Loading...");
        weatherIcon.setIcon(null);
        weatherDescLabel.setText(" ");
        forecastPanel.removeAll(); // Clear old forecast

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private WeatherData currentData;
            private List<ForecastData> forecastData;
            private boolean success = false;

            @Override
            protected Void doInBackground() {
                currentData = weatherApiClient.getWeatherData(city);
                if (currentData != null) {
                    forecastData = weatherApiClient.getForecastData(city);
                    success = forecastData != null;
                }
                return null;
            }

            @Override
            protected void done() {
                if (success) {
                    updateUI(city, currentData, forecastData);
                } else {
                    // Handle city not found or other errors
                    cityLabel.setText("City not found");
                    tempLabel.setText("--°C");
                    weatherDescLabel.setText(" ");
                    windValueLabel.setText("-- km/h");
                    humidityValueLabel.setText("--%");
                    visibilityValueLabel.setText("-- km");
                    weatherIcon.setIcon(null);
                    forecastPanel.removeAll();
                    forecastPanel.revalidate();
                    forecastPanel.repaint();
                }
            }
        };
        worker.execute();
    }

    private void updateUI(String city, WeatherData data, List<ForecastData> forecastDataList) {
        // Update current weather
        cityLabel.setText(capitalize(city));
        tempLabel.setText(String.format("%.0f°C", data.getTemperature()));
        weatherDescLabel.setText(capitalize(data.getWeatherDescription()));
        windValueLabel.setText(String.format("%.2f km/h", data.getWindSpeed()));
        humidityValueLabel.setText(data.getHumidity() + "%");
        visibilityValueLabel.setText((data.getVisibility() / 1000) + " km");
        weatherIcon.setIcon(loadWeatherIcon(data.getIconCode()));

        // Update forecast panel
        forecastPanel.removeAll();
        for (ForecastData day : forecastDataList) {
            forecastPanel.add(createForecastDayPanel(day));
        }
        forecastPanel.revalidate();
        forecastPanel.repaint();
    }

    private JPanel createForecastDayPanel(ForecastData data) {
        JPanel dayPanel = new JPanel();
        dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
        dayPanel.setBackground(COMPONENT_COLOR);
        dayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel dayLabel = new JLabel(data.getDayOfWeek());
        dayLabel.setFont(BOLD_FONT);
        dayLabel.setForeground(TEXT_COLOR);
        dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel(loadWeatherIcon(data.getIconCode(), 50, 50));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tempLabel = new JLabel(String.format("%.0f°", data.getTemperature()));
        tempLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 22));
        tempLabel.setForeground(TEXT_COLOR);
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dayPanel.add(dayLabel);
        dayPanel.add(Box.createVerticalStrut(10)); // Spacer
        dayPanel.add(iconLabel);
        dayPanel.add(Box.createVerticalStrut(10)); // Spacer
        dayPanel.add(tempLabel);

        return dayPanel;
    }

    private String capitalize(String text) {
        // ... (no changes in this method) ...
        if (text == null || text.isEmpty()) return text;
        String[] words = text.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            capitalized.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase(Locale.ROOT))
                    .append(" ");
        }
        return capitalized.toString().trim();
    }

    // Overloaded method for scaling forecast icons
    private ImageIcon loadWeatherIcon(String iconCode, int width, int height) {
        ImageIcon originalIcon = loadWeatherIcon(iconCode);
        if (originalIcon != null) {
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
        return null;
    }

    private ImageIcon loadWeatherIcon(String iconCode) {
        // ... (no changes in this method) ...
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

