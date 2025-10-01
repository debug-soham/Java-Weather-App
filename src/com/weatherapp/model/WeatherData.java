package com.weatherapp.model;

public class WeatherData {
    private final String cityName; // Added this field
    private final double temperature;
    private final String description;
    private final String iconCode;
    private final double windSpeed;
    private final int humidity;
    private final long sunrise;
    private final long sunset;

    public WeatherData(String cityName, double temperature, String description, String iconCode, double windSpeed, int humidity, long sunrise, long sunset) {
        this.cityName = cityName; // Added to constructor
        this.temperature = temperature;
        this.description = description;
        this.iconCode = iconCode;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    // Added getter for cityName
    public String getCityName() {
        return cityName;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    public String getIconCode() {
        return iconCode;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getHumidity() {
        return humidity;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }
}

