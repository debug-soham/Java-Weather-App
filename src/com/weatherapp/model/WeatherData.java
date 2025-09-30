package com.weatherapp.model;

/**
 * A Plain Old Java Object (POJO) to represent the weather data for a specific location.
 * This class encapsulates all the relevant weather information fetched from the API.
 */
public class WeatherData {
    private double temperature;
    private int humidity;
    private double windSpeed;
    private long visibility;
    private String iconCode;
    private String weatherDescription; // New field for the description

    // Getters and Setters for all fields

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public long getVisibility() {
        return visibility;
    }

    public void setVisibility(long visibility) {
        this.visibility = visibility;
    }

    public String getIconCode() {
        return iconCode;
    }

    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }
}

