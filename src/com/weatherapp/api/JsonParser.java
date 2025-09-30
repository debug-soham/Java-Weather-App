package com.weatherapp.api;

import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class responsible for parsing JSON responses from the OpenWeatherMap API.
 */
public class JsonParser {

    /**
     * Parses the JSON string for current weather into a WeatherData object.
     * @param json The JSON string response from the API.
     * @return A WeatherData object, or null if parsing fails.
     */
    public static WeatherData parseWeatherData(String json) {
        try {
            // ... (existing code for parsing current weather - no changes needed here) ...
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(json);

            JSONObject main = (JSONObject) jsonObject.get("main");
            double temp = (double) main.get("temp");
            long humidity = (long) main.get("humidity");

            JSONObject wind = (JSONObject) jsonObject.get("wind");
            double windSpeed = (double) wind.get("speed");

            long visibility = (long) jsonObject.get("visibility");

            JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);
            String iconCode = (String) weather.get("icon");
            String weatherDescription = (String) weather.get("description");

            WeatherData weatherData = new WeatherData();
            weatherData.setTemperature(temp);
            weatherData.setHumidity((int) humidity);
            weatherData.setWindSpeed(windSpeed);
            weatherData.setVisibility(visibility);
            weatherData.setIconCode(iconCode);
            weatherData.setWeatherDescription(weatherDescription);

            return weatherData;
        } catch (Exception e) {
            System.err.println("Error parsing current weather JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parses the JSON string for 5-day forecast into a list of ForecastData objects.
     * It selects one forecast per day, aiming for the midday (12:00) data point.
     * @param json The JSON string response from the API.
     * @return A list of ForecastData objects, one for each of the next 5 days.
     */
    public static List<ForecastData> parseForecastData(String json) {
        List<ForecastData> forecastList = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            JSONArray list = (JSONArray) jsonObject.get("list");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < list.size(); i++) {
                JSONObject forecast = (JSONObject) list.get(i);
                String dt_txt = (String) forecast.get("dt_txt");

                // We only want one forecast per day, let's pick the one at 12:00:00
                if (dt_txt.contains("12:00:00")) {
                    LocalDateTime dateTime = LocalDateTime.parse(dt_txt, formatter);
                    String dayOfWeek = dateTime.getDayOfWeek().toString().substring(0, 3); // "MON", "TUE", etc.

                    JSONObject main = (JSONObject) forecast.get("main");
                    double temp = (double) main.get("temp");

                    JSONArray weatherArray = (JSONArray) forecast.get("weather");
                    JSONObject weather = (JSONObject) weatherArray.get(0);
                    String iconCode = (String) weather.get("icon");

                    forecastList.add(new ForecastData(dayOfWeek, temp, iconCode));
                }
            }
            return forecastList;
        } catch (Exception e) {
            System.err.println("Error parsing forecast JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

