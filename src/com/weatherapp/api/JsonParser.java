package com.weatherapp.api;

import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to parse JSON responses from the OpenWeatherMap API.
 */
public class JsonParser {

    /**
     * Parses the JSON response for current weather into a WeatherData object.
     * @param jsonResponse The JSONObject containing the current weather data.
     * @return A WeatherData object, or null if parsing fails.
     */
    public static WeatherData parseCurrentWeather(JSONObject jsonResponse) {
        if (jsonResponse == null) return null;

        try {
            WeatherData data = new WeatherData();
            JSONObject main = (JSONObject) jsonResponse.get("main");
            JSONObject wind = (JSONObject) jsonResponse.get("wind");
            JSONObject sys = (JSONObject) jsonResponse.get("sys");
            JSONArray weatherArray = (JSONArray) jsonResponse.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);

            data.setTemperature((Double) main.get("temp"));
            data.setHumidity((Long) main.get("humidity"));
            data.setWindSpeed((Double) wind.get("speed"));
            data.setDescription((String) weather.get("description"));
            data.setSunrise((Long) sys.get("sunrise"));
            data.setSunset((Long) sys.get("sunset"));
            data.setIconCode((String) weather.get("icon"));

            return data;
        } catch (Exception e) {
            System.err.println("Error parsing current weather JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parses the JSON response for a 5-day forecast into a list of ForecastData objects.
     * @param jsonResponse The JSONObject containing the forecast data.
     * @return A List of ForecastData objects, or an empty list if parsing fails.
     */
    public static List<ForecastData> parseFiveDayForecast(JSONObject jsonResponse) {
        List<ForecastData> forecastList = new ArrayList<>();
        if (jsonResponse == null) return forecastList;

        try {
            JSONArray list = (JSONArray) jsonResponse.get("list");
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");

            // The API returns data every 3 hours. We'll pick one entry per day (e.g., at noon).
            for (int i = 7; i < list.size(); i += 8) {
                JSONObject forecast = (JSONObject) list.get(i);
                long dt = (Long) forecast.get("dt");
                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt), ZoneId.systemDefault());

                JSONObject main = (JSONObject) forecast.get("main");
                double temp = (Double) main.get("temp");

                JSONArray weatherArray = (JSONArray) forecast.get("weather");
                JSONObject weather = (JSONObject) weatherArray.get(0);
                String iconCode = (String) weather.get("icon");

                // THIS IS THE FIX: Pass the raw iconCode, not the full path.
                forecastList.add(new ForecastData(dateTime.format(dayFormatter), temp, iconCode));
            }
        } catch (Exception e) {
            System.err.println("Error parsing forecast JSON: " + e.getMessage());
        }
        return forecastList;
    }
}

