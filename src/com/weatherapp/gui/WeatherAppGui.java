package com.weatherapp.gui;

import com.weatherapp.api.JsonParser;
import com.weatherapp.api.WeatherApiClient;
import com.weatherapp.gui.component.RoundedPanel;
import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;
import com.weatherapp.util.FontLoader;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The main graphical user interface for the Weather Information App.
 * This class is responsible for building and managing all UI components with an aesthetic landscape layout.
 */
public class WeatherAppGui extends JFrame {

    // Define colors for the dark theme
    private static final Color BACKGROUND_COLOR = new Color(0x1F1F1F);
    private static final Color COMPONENT_COLOR = new Color(0x2B2B2B);
    private static final Color TEXT_COLOR = new Color(0xF0F0F0);
    private static final Color PLACEHOLDER_TEXT_COLOR = new Color(0x505050); // Custom placeholder color


    // Declare fonts using the custom FontLoader
    private static final Font FONT_BOLD_48 = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 48f);
    private static final Font FONT_BOLD_24 = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 24f);
    private static final Font FONT_BOLD_20 = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 20f);
    private static final Font FONT_REGULAR_18 = FontLoader.loadFont("/fonts/Montserrat-Regular.ttf", 18f);
    private static final Font FONT_REGULAR_16 = FontLoader.loadFont("/fonts/Montserrat-Regular.ttf", 16f);
    private static final Font FONT_REGULAR_14 = FontLoader.loadFont("/fonts/Montserrat-Regular.ttf", 14f);

    private final WeatherApiClient apiClient;
    private JLabel cityLabel, tempLabel, weatherIcon, descriptionLabel;
    private JLabel windValueLabel, humidityValueLabel, sunriseValueLabel, sunsetValueLabel;
    private JPanel forecastPanel;

    public WeatherAppGui() {
        this.apiClient = new WeatherApiClient();
        setTitle("Weather Information App");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);
        createUI();

        // Load weather data for a default city on startup
        updateWeatherData("Mumbai");
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(mainPanel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // --- Search Field with Corrected Placeholder Logic ---
        final String placeholderText = "Search City";
        JTextField searchField = new JTextField(placeholderText);
        searchField.setFont(FONT_REGULAR_16);
        searchField.setPreferredSize(new Dimension(350, 40));
        searchField.setBackground(COMPONENT_COLOR);
        searchField.setForeground(PLACEHOLDER_TEXT_COLOR);
        searchField.setCaretColor(TEXT_COLOR);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(placeholderText)) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(placeholderText);
                    searchField.setForeground(PLACEHOLDER_TEXT_COLOR);
                }
            }
        });

        searchField.addActionListener(e -> updateWeatherData(searchField.getText()));
        searchPanel.add(searchField);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Overhauled center panel using GridBagLayout for precise control
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();

        // -- Current Weather Panel (Now a RoundedPanel) --
        RoundedPanel currentWeatherPanel = new RoundedPanel(null, 25);
        currentWeatherPanel.setLayout(new BoxLayout(currentWeatherPanel, BoxLayout.Y_AXIS));
        currentWeatherPanel.setBackground(COMPONENT_COLOR);
        currentWeatherPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));


        cityLabel = new JLabel("City Name");
        cityLabel.setFont(FONT_BOLD_24);
        cityLabel.setForeground(TEXT_COLOR);
        cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel tempIconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        tempIconPanel.setBackground(COMPONENT_COLOR); // Match parent panel background
        tempLabel = new JLabel("--°C");
        tempLabel.setFont(FONT_BOLD_48);
        tempLabel.setForeground(TEXT_COLOR);
        weatherIcon = new JLabel();
        tempIconPanel.add(tempLabel);
        tempIconPanel.add(weatherIcon);

        descriptionLabel = new JLabel("Weather Description");
        descriptionLabel.setFont(FONT_REGULAR_18);
        descriptionLabel.setForeground(TEXT_COLOR);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        currentWeatherPanel.add(cityLabel);
        currentWeatherPanel.add(Box.createVerticalStrut(15));
        currentWeatherPanel.add(tempIconPanel);
        currentWeatherPanel.add(Box.createVerticalStrut(15));
        currentWeatherPanel.add(descriptionLabel);

        // --- Wrapper panel to center the currentWeatherPanel ---
        JPanel currentWeatherWrapper = new JPanel(new GridBagLayout());
        currentWeatherWrapper.setBackground(BACKGROUND_COLOR);
        currentWeatherWrapper.add(currentWeatherPanel);


        // -- Today's Highlights Panel --
        JPanel highlightsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        highlightsPanel.setBackground(BACKGROUND_COLOR);
        Border highlightsTitle = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "Today's Highlights", 0, 0, FONT_REGULAR_16, TEXT_COLOR);
        highlightsPanel.setBorder(BorderFactory.createCompoundBorder(highlightsTitle, BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        windValueLabel = new JLabel("-- km/h");
        highlightsPanel.add(createHighlightPanel("Wind Status", windValueLabel));
        humidityValueLabel = new JLabel("-- %");
        highlightsPanel.add(createHighlightPanel("Humidity", humidityValueLabel));
        sunriseValueLabel = new JLabel("--:-- AM");
        highlightsPanel.add(createHighlightPanel("Sunrise", sunriseValueLabel));
        sunsetValueLabel = new JLabel("--:-- PM");
        highlightsPanel.add(createHighlightPanel("Sunset", sunsetValueLabel));

        // Add the wrapper and highlights to the center panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Wrapper takes up remaining horizontal space
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 20, 20);
        centerPanel.add(currentWeatherWrapper, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0; // Highlights panel takes only its preferred size
        gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(highlightsPanel, gbc);

        // -- Forecast Panel --
        forecastPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        forecastPanel.setBackground(BACKGROUND_COLOR);
        Border forecastTitle = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "5-Day Forecast", 0, 0, FONT_REGULAR_16, TEXT_COLOR);
        forecastPanel.setBorder(BorderFactory.createCompoundBorder(forecastTitle, BorderFactory.createEmptyBorder(10, 0, 10, 0)));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span both columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(forecastPanel, gbc);
    }

    private JPanel createHighlightPanel(String title, JLabel valueLabel) {
        JPanel panel = new RoundedPanel(new GridBagLayout(), 25);
        panel.setBackground(COMPONENT_COLOR);

        GridBagConstraints pGbc = new GridBagConstraints();
        pGbc.gridx = 0;
        pGbc.anchor = GridBagConstraints.CENTER;
        pGbc.insets = new Insets(10, 15, 10, 15);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_REGULAR_14);
        titleLabel.setForeground(TEXT_COLOR);
        pGbc.gridy = 0;
        panel.add(titleLabel, pGbc);
        valueLabel.setFont(FONT_BOLD_20);
        valueLabel.setForeground(TEXT_COLOR);
        pGbc.gridy = 1;
        pGbc.insets = new Insets(5, 15, 10, 15);
        panel.add(valueLabel, pGbc);
        return panel;
    }

    private void updateWeatherData(String cityName) {
        // Do not perform search if the text is the placeholder or empty
        if (cityName.equalsIgnoreCase("Search City") || cityName.trim().isEmpty()) {
            return;
        }

        JSONObject currentWeatherDataJson = apiClient.getCurrentWeather(cityName);
        WeatherData currentWeatherData = JsonParser.parseCurrentWeather(currentWeatherDataJson);
        if (currentWeatherData != null) {
            cityLabel.setText(cityName);
            tempLabel.setText(String.format("%.0f°C", currentWeatherData.getTemperature()));
            descriptionLabel.setText(currentWeatherData.getDescription());
            windValueLabel.setText(String.format("%.2f km/h", currentWeatherData.getWindSpeed()));
            humidityValueLabel.setText(currentWeatherData.getHumidity() + " %");
            sunriseValueLabel.setText(convertTimestampToTime(currentWeatherData.getSunrise()));
            sunsetValueLabel.setText(convertTimestampToTime(currentWeatherData.getSunset()));
            loadWeatherIcon(weatherIcon, currentWeatherData.getIconCode(), 100);
            JSONObject forecastDataJson = apiClient.getFiveDayForecast(cityName);
            List<ForecastData> forecastList = JsonParser.parseFiveDayForecast(forecastDataJson);
            updateForecastPanel(forecastList);
        } else {
            JOptionPane.showMessageDialog(this, "Could not find city '" + cityName + "'. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateForecastPanel(List<ForecastData> forecastList) {
        forecastPanel.removeAll();
        for (ForecastData data : forecastList) {
            JPanel dayPanel = new RoundedPanel(new GridBagLayout(), 25);
            dayPanel.setBackground(COMPONENT_COLOR);
            dayPanel.setPreferredSize(new Dimension(110, 140));

            GridBagConstraints dGbc = new GridBagConstraints();
            dGbc.gridx = 0;
            dGbc.anchor = GridBagConstraints.CENTER;
            JLabel dayLabel = new JLabel(data.getDayOfWeek());
            dayLabel.setFont(FONT_REGULAR_16);
            dayLabel.setForeground(TEXT_COLOR);
            dGbc.gridy = 0;
            dGbc.insets = new Insets(10, 0, 0, 0);
            dayPanel.add(dayLabel, dGbc);
            JLabel iconLabel = new JLabel();
            loadWeatherIcon(iconLabel, data.getIconCode(), 50);
            dGbc.gridy = 1;
            dGbc.insets = new Insets(5, 0, 5, 0);
            dayPanel.add(iconLabel, dGbc);
            JLabel tempLabel = new JLabel(String.format("%.0f°", data.getTemperature()));
            tempLabel.setFont(FONT_BOLD_20);
            tempLabel.setForeground(TEXT_COLOR);
            dGbc.gridy = 2;
            dGbc.insets = new Insets(0, 0, 10, 0);
            dayPanel.add(tempLabel, dGbc);
            forecastPanel.add(dayPanel);
        }
        forecastPanel.revalidate();
        forecastPanel.repaint();
    }

    private String convertTimestampToTime(long timestamp) {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    private void loadWeatherIcon(JLabel iconLabel, String iconCode, int size) {
        String path = "/assets/" + iconCode + ".png";
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image scaledImage = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Could not find icon file: " + path);
        }
    }
}

