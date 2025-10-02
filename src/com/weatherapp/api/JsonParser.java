package com.weatherapp.api;

import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static WeatherData parseCurrentWeather(JSONObject weatherDataJson) {
        if (weatherDataJson == null) return null;

        try {
            String cityName = (String) weatherDataJson.get("name");
            JSONArray weatherArray = (JSONArray) weatherDataJson.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);
            String description = (String) weather.get("description");
            String iconCode = (String) weather.get("icon");
            JSONObject main = (JSONObject) weatherDataJson.get("main");
            double temperature = ((Number) main.get("temp")).doubleValue();
            int humidity = ((Number) main.get("humidity")).intValue();
            JSONObject wind = (JSONObject) weatherDataJson.get("wind");
            double windSpeed = ((Number) wind.get("speed")).doubleValue();
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
        if (forecastDataJson == null) return new ArrayList<>();

        List<ForecastData> forecastList = new ArrayList<>();
        JSONArray list = (JSONArray) forecastDataJson.get("list");

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");

        for (Object item : list) {
            JSONObject forecast = (JSONObject) item;
            long timestamp = (long) forecast.get("dt");
            LocalDateTime forecastDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());

            // We only want one forecast per day, and not for today. Let's pick the one around noon.
            if (!forecastDateTime.toLocalDate().isEqual(today) && forecastDateTime.getHour() >= 12) {
                // Check if we already added a forecast for this day
                boolean dayAlreadyAdded = forecastList.stream()
                        .anyMatch(f -> f.getDayOfWeek().equals(forecastDateTime.format(dayFormatter)));

                if (!dayAlreadyAdded && forecastList.size() < 5) {
                    JSONObject main = (JSONObject) forecast.get("main");
                    double temperature = ((Number) main.get("temp")).doubleValue();
                    JSONArray weatherArray = (JSONArray) forecast.get("weather");
                    JSONObject weather = (JSONObject) weatherArray.get(0);
                    String iconCode = (String) weather.get("icon");

                    forecastList.add(new ForecastData(forecastDateTime.format(dayFormatter), temperature, iconCode));
                }
            }
        }
        return forecastList;
    }
}

