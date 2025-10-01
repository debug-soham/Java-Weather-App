package com.weatherapp.gui;

import com.weatherapp.api.JsonParser;
import com.weatherapp.api.WeatherApiClient;
import com.weatherapp.gui.component.RoundedPanel;
import com.weatherapp.model.WeatherData;
import com.weatherapp.util.FontLoader;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The main graphical user interface for the Weather Information App.
 * This class meticulously replicates a high-fidelity design using advanced layout management.
 * Note: This version excludes the 5-day forecast to focus on the top-half design.
 */
public class WeatherAppGui extends JFrame {

    // Define colors from the design
    private static final Color BACKGROUND_COLOR = new Color(0x1C1C1E);
    private static final Color COMPONENT_COLOR = new Color(0x2C2C2E);
    private static final Color TEXT_COLOR = new Color(0xFFFFFF);
    private static final Color PLACEHOLDER_TEXT_COLOR = new Color(0x8E8E93);

    // Declare fonts from the design
    private static final Font FONT_REGULAR_16 = FontLoader.loadFont("/fonts/Montserrat-Regular.ttf", 16f);
    private static final Font FONT_BOLD_20 = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 20f);
    private static final Font FONT_BOLD_60 = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 60f);
    private static final Font FONT_REGULAR_22 = FontLoader.loadFont("/fonts/Montserrat-Regular.ttf", 22f);
    private static final Font FONT_BOLD_30 = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 30f);

    private final WeatherApiClient apiClient;
    private JLabel cityLabel, tempLabel, descriptionLabel, weatherIconLabel;
    private JLabel windValueLabel, humidityValueLabel, sunriseValueLabel, sunsetValueLabel;

    public WeatherAppGui() {
        this.apiClient = new WeatherApiClient();
        setTitle("Weather Information App");
        setSize(950, 480); // Adjusted size for top-half only
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        createUI();
        updateWeatherData("Mumbai"); // Load default city as per design
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        // -- Search Bar Panel --
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span both columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 16, 25, 16); // Bottom margin
        mainPanel.add(createSearchPanel(), gbc);

        // -- Left Column Panels --
        JPanel leftColumn = new JPanel(new GridBagLayout());
        leftColumn.setOpaque(false);
        GridBagConstraints leftGbc = new GridBagConstraints();

        // City Name Panel
        leftGbc.gridy = 0;
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.insets = new Insets(7, 0, 20, 0); // Bottom margin
        leftColumn.add(createCityPanel(), leftGbc);

        // Current Weather Panel
        leftGbc.gridy = 1;
        leftGbc.weighty = 1.0; // Take up remaining vertical space
        leftGbc.fill = GridBagConstraints.BOTH;
        leftColumn.add(createWeatherPanel(), leftGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.65; // Final Adjustment: Left column is wider
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 7, 0, 10); // Right margin
        mainPanel.add(leftColumn, gbc);

        // -- Right Column (Highlights) --
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.35; // Final Adjustment: Right column is narrower
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 13);
        mainPanel.add(createHighlightsPanel(), gbc);
    }

    private JPanel createSearchPanel() {
        RoundedPanel searchPanel = new RoundedPanel(new BorderLayout(15, 0), 20);
        searchPanel.setBackground(COMPONENT_COLOR);
        searchPanel.setBorder(new EmptyBorder(8, 12, 8, 15));

        JLabel searchIcon = new JLabel(new ImageIcon(getClass().getResource("/icons/search.png")));
        searchPanel.add(searchIcon, BorderLayout.WEST);

        final String placeholder = "Search your location";
        JTextField searchField = new JTextField(placeholder);
        searchField.setFont(FONT_REGULAR_16);
        searchField.setForeground(PLACEHOLDER_TEXT_COLOR);
        searchField.setBackground(COMPONENT_COLOR);
        searchField.setCaretColor(TEXT_COLOR);
        searchField.setBorder(null);

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(PLACEHOLDER_TEXT_COLOR);
                }
            }
        });
        searchField.addActionListener(e -> updateWeatherData(searchField.getText()));

        searchPanel.add(searchField, BorderLayout.CENTER);
        return searchPanel;
    }

    private JPanel createCityPanel() {
        RoundedPanel cityPanel = new RoundedPanel(new FlowLayout(FlowLayout.LEFT, 15, 0), 20);
        cityPanel.setBackground(COMPONENT_COLOR);
        cityPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel locationIcon = new JLabel(new ImageIcon(getClass().getResource("/icons/location.png")));
        cityLabel = new JLabel("City Name");
        cityLabel.setFont(FONT_BOLD_30);
        cityLabel.setForeground(TEXT_COLOR);

        cityPanel.add(locationIcon);
        cityPanel.add(cityLabel);
        return cityPanel;
    }

    private JPanel createWeatherPanel() {
        RoundedPanel weatherPanel = new RoundedPanel(new GridBagLayout(), 20);
        weatherPanel.setBackground(COMPONENT_COLOR);
        weatherPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        tempLabel = new JLabel("--°C");
        tempLabel.setFont(FONT_BOLD_60);
        tempLabel.setForeground(TEXT_COLOR);

        descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(FONT_REGULAR_22);
        descriptionLabel.setForeground(TEXT_COLOR);

        textPanel.add(tempLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descriptionLabel);

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        weatherPanel.add(textPanel, gbc);

        weatherIconLabel = new JLabel();
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 20, 0, 0); // Final Adjustment: Add gap between text and icon
        weatherPanel.add(weatherIconLabel, gbc);

        return weatherPanel;
    }

    private JPanel createHighlightsPanel() {
        JPanel highlightsWrapper = new JPanel(new GridBagLayout());
        highlightsWrapper.setOpaque(false);
        GridBagConstraints hGbc = new GridBagConstraints();

        JLabel title = new JLabel("Today's Highlight");
        title.setFont(FONT_BOLD_20);
        title.setForeground(TEXT_COLOR);
        hGbc.gridx = 0;
        hGbc.gridy = 0;
        hGbc.anchor = GridBagConstraints.WEST;
        hGbc.insets = new Insets(0, 5, 15, 0);
        highlightsWrapper.add(title, hGbc);

        JPanel highlightsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        highlightsGrid.setOpaque(false);

        windValueLabel = new JLabel("-- km/h");
        highlightsGrid.add(createHighlightItem("Wind Status", windValueLabel, "/icons/wind.png"));

        sunriseValueLabel = new JLabel("--:-- AM");
        highlightsGrid.add(createHighlightItem("Sunrise", sunriseValueLabel, "/icons/sunrise.png"));

        humidityValueLabel = new JLabel("-- %");
        highlightsGrid.add(createHighlightItem("Humidity", humidityValueLabel, "/icons/humidity.png"));

        sunsetValueLabel = new JLabel("--:-- PM");
        highlightsGrid.add(createHighlightItem("Sunset", sunsetValueLabel, "/icons/sunset.png"));

        hGbc.gridy = 1;
        hGbc.weighty = 1.0;
        hGbc.fill = GridBagConstraints.BOTH;
        highlightsWrapper.add(highlightsGrid, hGbc);
        return highlightsWrapper;
    }

    private JPanel createHighlightItem(String title, JLabel valueLabel, String iconPath) {
        RoundedPanel panel = new RoundedPanel(new GridBagLayout(), 20);
        panel.setBackground(COMPONENT_COLOR);
        panel.setBorder(new EmptyBorder(15, 25, 15, 25)); // Increased horizontal padding

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource(iconPath)));
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(iconLabel, gbc);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_REGULAR_16);
        titleLabel.setForeground(PLACEHOLDER_TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(FONT_BOLD_20);
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(valueLabel);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0); // Increased space between icon and text
        panel.add(textPanel, gbc);
        return panel;
    }

    private void updateWeatherData(String cityName) {
        if (cityName.equalsIgnoreCase("Search your location") || cityName.trim().isEmpty()) {
            return;
        }
        JSONObject dataJson = apiClient.getCurrentWeather(cityName);
        WeatherData data = JsonParser.parseCurrentWeather(dataJson);
        if (data != null) {
            cityLabel.setText(data.getCityName());
            tempLabel.setText(String.format("%.0f°C", data.getTemperature()));
            descriptionLabel.setText(data.getDescription());
            windValueLabel.setText(String.format("%.2f km/h", data.getWindSpeed()));
            humidityValueLabel.setText(data.getHumidity() + " %");
            sunriseValueLabel.setText(convertTimestampToTime(data.getSunrise()));
            sunsetValueLabel.setText(convertTimestampToTime(data.getSunset()));
            loadWeatherIcon(weatherIconLabel, data.getIconCode(), 120);
        } else {
            JOptionPane.showMessageDialog(this, "Could not find city: " + cityName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String convertTimestampToTime(long timestamp) {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    private void loadWeatherIcon(JLabel iconLabel, String iconCode, int size) {
        String path = "/assets/" + iconCode + ".png";
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            iconLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            System.err.println("Could not find icon file: " + path);
        }
    }
}

