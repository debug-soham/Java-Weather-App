package com.weatherapp.api;

import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static WeatherData parseCurrentWeather(JSONObject weatherDataJson) {
        if (weatherDataJson == null) return null;

        try {
            // Get the city name
            String cityName = (String) weatherDataJson.get("name");

            // Get weather description and icon
            JSONArray weatherArray = (JSONArray) weatherDataJson.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);
            String description = (String) weather.get("description");
            String iconCode = (String) weather.get("icon");

            // Get temperature
            JSONObject main = (JSONObject) weatherDataJson.get("main");
            double temperature = ((Number) main.get("temp")).doubleValue();
            int humidity = ((Number) main.get("humidity")).intValue();

            // Get wind speed
            JSONObject wind = (JSONObject) weatherDataJson.get("wind");
            double windSpeed = ((Number) wind.get("speed")).doubleValue();

            // Get sunrise and sunset
            JSONObject sys = (JSONObject) weatherDataJson.get("sys");
            long sunrise = (long) sys.get("sunrise");
            long sunset = (long) sys.get("sunset");

            return new WeatherData(cityName, temperature, description, iconCode, windSpeed, humidity, sunrise, sunset);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ForecastData> parseFiveDayForecast(JSONObject forecastDataJson) {
        // This method is not used by the current "pixel-perfect" GUI but is kept for completeness.
        return new ArrayList<>();
    }
}

