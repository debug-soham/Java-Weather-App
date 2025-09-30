package com.weatherapp.model;

/**
 * A Plain Old Java Object (POJO) to represent the weather forecast for a single day.
 * This class encapsulates the day of the week, temperature, and icon code.
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
