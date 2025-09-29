package com.weatherapp.model;

/**
 * A plain old Java object (POJO) to store the weather data retrieved from the API.
 * This class encapsulates all the weather-related information for a specific location.
 */
public class WeatherData {
    private double temperature;
    private long humidity;
    private double windSpeed;
    private long visibility;
    private String description;
    private String iconCode;

    public WeatherData() {}

    // --- Getters ---
    public double getTemperature() { return temperature; }
    public long getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public long getVisibility() { return visibility; }
    public String getDescription() { return description; }
    public String getIconCode() { return iconCode; }

    // --- Setters ---
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setHumidity(long humidity) { this.humidity = humidity; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    public void setVisibility(long visibility) { this.visibility = visibility; }
    public void setDescription(String description) { this.description = description; }
    public void setIconCode(String iconCode) { this.iconCode = iconCode; }
}
