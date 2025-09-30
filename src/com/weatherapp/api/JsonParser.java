package com.weatherapp.api;

import com.weatherapp.model.WeatherData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * A utility class responsible for parsing the JSON response from the OpenWeatherMap API.
 * It extracts relevant weather data and populates a WeatherData object.
 */
public class JsonParser {

    /**
     * Parses the JSON string from the weather API into a WeatherData object.
     * @param json The JSON string response from the API.
     * @return A WeatherData object populated with information, or null if parsing fails.
     */
    public static WeatherData parseWeatherData(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(json);

            // Extract main weather data object
            JSONObject main = (JSONObject) jsonObject.get("main");
            double temp = (double) main.get("temp");
            long humidity = (long) main.get("humidity");

            // Extract wind data object
            JSONObject wind = (JSONObject) jsonObject.get("wind");
            double windSpeed = (double) wind.get("speed");

            // Extract visibility (it's in meters)
            long visibility = (long) jsonObject.get("visibility");

            // Extract weather array for icon code and description
            JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);
            String iconCode = (String) weather.get("icon");
            String weatherDescription = (String) weather.get("description"); // New line

            // Create and populate the WeatherData object
            WeatherData weatherData = new WeatherData();
            weatherData.setTemperature(temp);
            weatherData.setHumidity((int) humidity);
            weatherData.setWindSpeed(windSpeed);
            weatherData.setVisibility(visibility);
            weatherData.setIconCode(iconCode);
            weatherData.setWeatherDescription(weatherDescription); // Set the new data

            return weatherData;
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

