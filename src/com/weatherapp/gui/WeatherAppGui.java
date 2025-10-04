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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * The main graphical user interface for the Weather Information App.
 * This class meticulously replicates a high-fidelity design using advanced layout management.
 */
public class WeatherAppGui extends JFrame {

    // Define colors from the design
    private static final Color BACKGROUND_COLOR = new Color(0x0D0F12); // Very Dark Blue
    private static final Color COMPONENT_COLOR = new Color(0x1C1F27);  // Dark Slate Blue
    private static final Color TEXT_COLOR = new Color(0xE2E8F0);      // Off-white/Light Gray
    private static final Color PLACEHOLDER_TEXT_COLOR = new Color(0x718096); // Muted Slate

    // Declare fonts from the design
    private static final Font FONT_REGULAR_16 = FontLoader.loadFont("/fonts/Montserrat-Regular.ttf", 16f);
    private static final Font FONT_BOLD_20 = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 20f);
    private static final Font FONT_BOLD_60 = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 60f);
    private static final Font FONT_REGULAR_22 = FontLoader.loadFont("/fonts/Montserrat-Regular.ttf", 22f);
    private static final Font FONT_BOLD_30 = FontLoader.loadFont("/fonts/Montserrat-Bold.ttf", 30f);

    private final WeatherApiClient apiClient;
    // Initialize the search history with default values
    private final LinkedList<String> searchHistory = new LinkedList<>(List.of("Mumbai", "Pune"));

    private JLabel cityLabel, tempLabel, descriptionLabel, weatherIconLabel;
    private JLabel windValueLabel, humidityValueLabel, sunriseValueLabel, sunsetValueLabel;
    private JPanel forecastPanel;
    private JLabel recentCity1Name, recentCity1Temp, recentCity1Icon;
    private JLabel recentCity2Name, recentCity2Temp, recentCity2Icon;
    private JPanel recentSearchesPanel;


    public WeatherAppGui() {
        this.apiClient = new WeatherApiClient();
        setTitle("Weather Information App");
        setSize(950, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        createUI();

        // Display the initial recent searches
        updateRecentSearchesPanel();

        // Load main weather data for a default city on startup
        updateWeatherData("Mumbai");
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        // -- Top Content --
        JPanel topPanel = createTopPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        mainPanel.add(topPanel, gbc);

        // -- Bottom Content (Forecast and Recent Searches) --
        JPanel bottomPanel = createBottomPanel();
        gbc.gridy = 1;
        // Removed weighty and changed anchor to pull the panel up
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(25, 0, 0, 0);
        mainPanel.add(bottomPanel, gbc);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints topGbc = new GridBagConstraints();

        // Search Bar
        topGbc.gridx = 0;
        topGbc.gridy = 0;
        topGbc.gridwidth = 2;
        topGbc.fill = GridBagConstraints.HORIZONTAL;
        topGbc.insets = new Insets(0, 0, 25, 0);
        topPanel.add(createSearchPanel(), topGbc);

        // Left Column
        JPanel leftColumn = createLeftColumnPanel();
        topGbc.gridy = 1;
        topGbc.gridwidth = 1;
        topGbc.weightx = 0.65;
        topGbc.fill = GridBagConstraints.BOTH;
        topGbc.insets = new Insets(0, 0, 0, 20);
        topPanel.add(leftColumn, topGbc);

        // Right Column
        JPanel rightColumn = createHighlightsPanel();
        topGbc.gridx = 1;
        topGbc.weightx = 0.35;
        topGbc.fill = GridBagConstraints.BOTH; // Ensure it fills the cell
        topPanel.add(rightColumn, topGbc);

        return topPanel;
    }

    private JPanel createBottomPanel() {
        // Switched to GridBagLayout for proportional sizing
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Forecast Panel (now smaller)
        forecastPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        forecastPanel.setOpaque(false);
        Border forecastTitleBorder = BorderFactory.createTitledBorder(
                new EmptyBorder(0, 0, 0, 0), "5-Day Forecast", 0, 0, FONT_REGULAR_16, TEXT_COLOR
        );
        forecastPanel.setBorder(new CompoundBorder(
                forecastTitleBorder,
                new EmptyBorder(10, 0, 0, 0) // Add 10px top padding
        ));

        // Recent Searches Panel
        recentSearchesPanel = createRecentSearchesPanel();

        gbc.gridx = 0;
        gbc.weightx = 0.65; // Forecast takes more space
        gbc.insets = new Insets(0, 0, 0, 20);
        bottomPanel.add(forecastPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.35; // Recent searches takes less space
        gbc.insets = new Insets(0, 0, 0, 0);
        bottomPanel.add(recentSearchesPanel, gbc);

        return bottomPanel;
    }

    private JPanel createRecentSearchesPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Border recentTitleBorder = BorderFactory.createTitledBorder(
                new EmptyBorder(0, 0, 0, 0), "Recent Searches", 0, 0, FONT_REGULAR_16, TEXT_COLOR
        );
        panel.setBorder(new CompoundBorder(
                recentTitleBorder,
                new EmptyBorder(10, 0, 0, 0) // Add 10px top padding
        ));

        recentCity1Name = new JLabel("--");
        recentCity1Temp = new JLabel("--°");
        recentCity1Icon = new JLabel();
        panel.add(createRecentCityItem(recentCity1Name, recentCity1Temp, recentCity1Icon));

        panel.add(Box.createVerticalStrut(10));

        recentCity2Name = new JLabel("--");
        recentCity2Temp = new JLabel("--°");
        recentCity2Icon = new JLabel();
        panel.add(createRecentCityItem(recentCity2Name, recentCity2Temp, recentCity2Icon));

        panel.add(Box.createVerticalGlue()); // Pushes items to the top

        return panel;
    }

    private JPanel createRecentCityItem(JLabel cityNameLabel, JLabel tempLabel, JLabel iconLabel) {
        RoundedPanel itemPanel = new RoundedPanel(new GridBagLayout(), 20);
        itemPanel.setBackground(COMPONENT_COLOR);
        // Increased vertical padding by increasing border size
        itemPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();

        cityNameLabel.setFont(FONT_BOLD_20);
        cityNameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        itemPanel.add(cityNameLabel, gbc);

        tempLabel.setFont(FONT_BOLD_20);
        tempLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        itemPanel.add(tempLabel, gbc);

        gbc.gridx = 2;
        itemPanel.add(iconLabel, gbc);

        return itemPanel;
    }

    private JPanel createSearchPanel() {
        RoundedPanel searchPanel = new RoundedPanel(new BorderLayout(10, 0), 20);
        searchPanel.setBackground(COMPONENT_COLOR);
        searchPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
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
            public void focusGained(FocusEvent e) { if (searchField.getText().equals(placeholder)) { searchField.setText(""); searchField.setForeground(TEXT_COLOR); } }
            public void focusLost(FocusEvent e) { if (searchField.getText().isEmpty()) { searchField.setText(placeholder); searchField.setForeground(PLACEHOLDER_TEXT_COLOR); } }
        });
        searchField.addActionListener(e -> updateWeatherData(searchField.getText()));
        searchPanel.add(searchField, BorderLayout.CENTER);
        return searchPanel;
    }

    private JPanel createLeftColumnPanel() {
        JPanel leftColumn = new JPanel(new GridBagLayout());
        leftColumn.setOpaque(false);
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.insets = new Insets(0, 0, 20, 0);
        leftColumn.add(createCityPanel(), leftGbc);
        leftGbc.gridy = 1;
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftColumn.add(createWeatherPanel(), leftGbc);
        return leftColumn;
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
        gbc.insets = new Insets(0, 0, 0, 20);
        weatherPanel.add(textPanel, gbc);
        weatherIconLabel = new JLabel();
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
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
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));
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
        gbc.insets = new Insets(0, 20, 0, 0);
        panel.add(textPanel, gbc);
        return panel;
    }

    private void updateWeatherData(String cityName) {
        if (cityName.equalsIgnoreCase("Search your location") || cityName.trim().isEmpty()) return;

        JSONObject currentDataJson = apiClient.getCurrentWeather(cityName);
        WeatherData currentData = JsonParser.parseCurrentWeather(currentDataJson);

        if (currentData != null) {
            // Update main display
            cityLabel.setText(currentData.getCityName());
            tempLabel.setText(String.format("%.0f°C", currentData.getTemperature()));
            descriptionLabel.setText(currentData.getDescription());
            windValueLabel.setText(String.format("%.2f km/h", currentData.getWindSpeed()));
            humidityValueLabel.setText(currentData.getHumidity() + " %");
            sunriseValueLabel.setText(convertTimestampToTime(currentData.getSunrise()));
            sunsetValueLabel.setText(convertTimestampToTime(currentData.getSunset()));
            loadWeatherIcon(weatherIconLabel, currentData.getIconCode(), 120);

            // Update search history and recent searches panel
            updateSearchHistory(currentData.getCityName());
            updateRecentSearchesPanel();

            // Update forecast
            JSONObject forecastDataJson = apiClient.getFiveDayForecast(cityName);
            List<ForecastData> forecastList = JsonParser.parseFiveDayForecast(forecastDataJson);
            updateForecastPanel(forecastList);
        } else {
            JOptionPane.showMessageDialog(this, "Could not find city: " + cityName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSearchHistory(String cityName) {
        // Prevent duplicates at the top of the list
        searchHistory.remove(cityName);
        // Add new city to the front
        searchHistory.addFirst(cityName);
        // Ensure history size does not exceed 2
        while (searchHistory.size() > 2) {
            searchHistory.removeLast();
        }
    }

    private void updateRecentSearchesPanel() {
        // Reset labels
        recentCity1Name.setText("--"); recentCity1Temp.setText("--°"); recentCity1Icon.setIcon(null);
        recentCity2Name.setText("--"); recentCity2Temp.setText("--°"); recentCity2Icon.setIcon(null);

        // Update with history using a background worker
        if (!searchHistory.isEmpty()) {
            updateRecentCity(searchHistory.get(0), recentCity1Name, recentCity1Temp, recentCity1Icon);
        }
        if (searchHistory.size() > 1) {
            updateRecentCity(searchHistory.get(1), recentCity2Name, recentCity2Temp, recentCity2Icon);
        }
    }

    private void updateRecentCity(String cityName, JLabel nameLabel, JLabel tempLabel, JLabel iconLabel) {
        SwingWorker<WeatherData, Void> worker = new SwingWorker<>() {
            @Override
            protected WeatherData doInBackground() {
                JSONObject data = apiClient.getCurrentWeather(cityName);
                return JsonParser.parseCurrentWeather(data);
            }

            @Override
            protected void done() {
                try {
                    WeatherData data = get();
                    if (data != null) {
                        nameLabel.setText(data.getCityName());
                        tempLabel.setText(String.format("%.0f°", data.getTemperature()));
                        loadWeatherIcon(iconLabel, data.getIconCode(), 40);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void updateForecastPanel(List<ForecastData> forecastList) {
        forecastPanel.removeAll();
        if (forecastList == null || forecastList.isEmpty()) return;
        for (ForecastData data : forecastList) {
            forecastPanel.add(createForecastItem(data));
        }
        forecastPanel.revalidate();
        forecastPanel.repaint();
    }

    private JPanel createForecastItem(ForecastData data) {
        RoundedPanel dayPanel = new RoundedPanel(new GridBagLayout(), 20);
        dayPanel.setBackground(COMPONENT_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel dayLabel = new JLabel(data.getDayOfWeek());
        dayLabel.setFont(FONT_REGULAR_16); // Smaller font for day
        dayLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 0;
        // Increased vertical insets for more height
        gbc.insets = new Insets(15, 0, 0, 0);
        dayPanel.add(dayLabel, gbc);
        JLabel iconLabel = new JLabel();
        loadWeatherIcon(iconLabel, data.getIconCode(), 50); // Smaller icon
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 8, 0);
        dayPanel.add(iconLabel, gbc);
        JLabel tempLabel = new JLabel(String.format("%.0f°", data.getTemperature()));
        tempLabel.setFont(FONT_BOLD_20); // Smaller font for temp
        tempLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 2;
        // Increased vertical insets for more height
        gbc.insets = new Insets(0, 0, 15, 0);
        dayPanel.add(tempLabel, gbc);
        return dayPanel;
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
            iconLabel.setIcon(null);
        }
    }
}

