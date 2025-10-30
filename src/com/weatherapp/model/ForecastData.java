package com.weatherapp.model;

/**
 * A data model class (POJO) that holds the essential weather information
 * for a single day in the 5-day forecast.
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

    // Getters for all private fields.
    public String getDayOfWeek() { return dayOfWeek; }
    public double getTemperature() { return temperature; }
    public String getIconCode() { return iconCode; }
}
