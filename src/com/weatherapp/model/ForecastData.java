package com.weatherapp.model;

/**
 * A simple data model class to hold the weather information for a single day in the forecast.
 */
public class ForecastData {
    private final String dayOfWeek;
    private final double temperature;
    private final String iconCode;

    public ForecastData(String dayOfWeek, double temperature, String iconCode) {
        this.dayOfWeek = dayOfWeek;
        this.temperature = temperature;
        this.iconCode = iconCode;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getIconCode() {
        return iconCode;
    }
}

