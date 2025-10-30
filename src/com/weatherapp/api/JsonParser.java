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

/**
 * A utility class to parse JSON responses from the OpenWeatherMap API
 * and convert them into structured data model objects.
 */
public class JsonParser {

    /**
     * Parses the JSON object for current weather data.
     * @param weatherDataJson The JSONObject containing current weather information.
     * @return A WeatherData object, or null if parsing fails.
     */
    public static WeatherData parseCurrentWeather(JSONObject weatherDataJson) {
        if (weatherDataJson == null) return null;

        try {
            // Navigate the JSON structure to extract required data fields.
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
            e.printStackTrace(); // Log parsing errors.
            return null;
        }
    }

    /**
     * Parses the 5-day forecast JSON to extract one forecast entry per day.
     * This robust logic ensures that exactly one forecast is chosen for each of the next 5 days,
     * regardless of the time of day the request is made.
     *
     * @param forecastDataJson The JSONObject containing the 5-day forecast list.
     * @return A list of ForecastData objects, one for each of the next 5 days.
     */
    public static List<ForecastData> parseFiveDayForecast(JSONObject forecastDataJson) {
        if (forecastDataJson == null) return new ArrayList<>();

        List<ForecastData> forecastList = new ArrayList<>();
        List<LocalDate> daysAdded = new ArrayList<>(); // Track which days have been added to avoid duplicates.

        JSONArray list = (JSONArray) forecastDataJson.get("list");
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");

        for (Object item : list) {
            JSONObject forecast = (JSONObject) item;
            long timestamp = (long) forecast.get("dt");
            LocalDateTime forecastDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
            LocalDate forecastDate = forecastDateTime.toLocalDate();

            // We only want one forecast per day, and not for today.
            // Check if we haven't already added a forecast for this day and if we still need more days.
            if (!forecastDate.isEqual(today) && !daysAdded.contains(forecastDate) && forecastList.size() < 5) {
                // This is the first entry we've found for this future day. Add it.
                daysAdded.add(forecastDate);

                JSONObject main = (JSONObject) forecast.get("main");
                double temperature = ((Number) main.get("temp")).doubleValue();
                JSONArray weatherArray = (JSONArray) forecast.get("weather");
                JSONObject weather = (JSONObject) weatherArray.get(0);
                String iconCode = (String) weather.get("icon");

                forecastList.add(new ForecastData(forecastDateTime.format(dayFormatter), temperature, iconCode));
            }
        }
        return forecastList;
    }
}
