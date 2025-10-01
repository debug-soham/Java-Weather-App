package com.weatherapp.model;

/**
 * A data model class that holds weather information for a single day in the forecast.
 */
public class ForecastData {
    private final String dayOfWeek;
    private final double temperature;
    private final String iconCode; // Changed from iconPath

    public ForecastData(String dayOfWeek, double temperature, String iconCode) {
        this.dayOfWeek = dayOfWeek;
        this.temperature = temperature;
        this.iconCode = iconCode; // Changed from iconPath
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public double getTemperature() {
        return temperature;
    }

    // Renamed method from getIconPath() to getIconCode()
    public String getIconCode() {
        return iconCode;
    }
}

