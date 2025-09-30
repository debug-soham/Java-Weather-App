package com.weatherapp.gui;

import com.weatherapp.api.WeatherApiClient;
import com.weatherapp.model.WeatherData;
import com.weatherapp.util.FontLoader; // Import the new utility class

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Locale;

/**
 * Represents the main Graphical User Interface for the Weather App.
 */
public class WeatherAppGui extends JFrame {

    // Define colors for the dark theme
    private static final Color BACKGROUND_COLOR = new Color(0x1F1F1F);
    private static final Color COMPONENT_COLOR = new Color(0x2B2B2B);
    private static final Color TEXT_COLOR = new Color(0xE0E0E0);

    // Define the new font family name
    private static final String FONT_FAMILY = "Montserrat";

    // Define fonts using the new font family
    private static final Font MAIN_FONT = new Font(FONT_FAMILY, Font.PLAIN, 16);
    private static final Font BOLD_FONT = new Font(FONT_FAMILY, Font.BOLD, 16);

    private final WeatherApiClient weatherApiClient;
    private JLabel cityLabel, tempLabel, weatherIcon, weatherDescLabel;
    private JLabel windValueLabel, humidityValueLabel, visibilityValueLabel;

    public WeatherAppGui() {
        super("Weather Information App");

        // Load custom fonts before creating any UI components
        FontLoader.loadFonts();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        weatherApiClient = new WeatherApiClient();
        createUI();
    }

    private void createUI() {
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

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        add(mainContentPanel, BorderLayout.CENTER);

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

        mainContentPanel.add(todayPanel, BorderLayout.NORTH);

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

        mainContentPanel.add(overviewContainer, BorderLayout.CENTER);

        searchField.addActionListener(e -> fetchWeatherData(searchField.getText()));
    }

    private JPanel createDetailPanel(String title, JLabel valueLabel) {
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
        cityLabel.setText("Loading...");
        weatherIcon.setIcon(null);
        weatherDescLabel.setText(" ");

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
                        weatherDescLabel.setText(" ");
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
        cityLabel.setText(capitalize(city));
        tempLabel.setText(String.format("%.0f°C", data.getTemperature()));
        weatherDescLabel.setText(capitalize(data.getWeatherDescription()));
        windValueLabel.setText(String.format("%.2f km/h", data.getWindSpeed()));
        humidityValueLabel.setText(data.getHumidity() + "%");
        visibilityValueLabel.setText((data.getVisibility() / 1000) + " km");
        weatherIcon.setIcon(loadWeatherIcon(data.getIconCode()));
    }

    private String capitalize(String text) {
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

