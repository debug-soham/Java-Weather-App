package com.weatherapp.api;

import com.weatherapp.model.WeatherData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A utility class to parse the JSON response from the OpenWeatherMap API.
 */
public class JsonParser {

    /**
     * Parses the JSON string from the weather API into a WeatherData object.
     * @param json The JSON string to parse.
     * @return A WeatherData object containing the parsed information, or null if parsing fails.
     */
    public static WeatherData parseWeatherData(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(json);

            // Create a WeatherData object to store the parsed data
            WeatherData weatherData = new WeatherData();

            // --- Extract data from the main part of the JSON object ---
            JSONObject main = (JSONObject) jsonObject.get("main");
            weatherData.setTemperature((Double) main.get("temp"));
            weatherData.setHumidity((Long) main.get("humidity"));

            // --- Extract data from the wind part ---
            JSONObject wind = (JSONObject) jsonObject.get("wind");
            weatherData.setWindSpeed((Double) wind.get("speed"));

            // --- Extract visibility ---
            weatherData.setVisibility((Long) jsonObject.get("visibility"));

            // --- Extract weather description and icon ---
            // The 'weather' key contains an array, so we get the first element
            JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
            if (weatherArray != null && !weatherArray.isEmpty()) {
                JSONObject weather = (JSONObject) weatherArray.get(0);
                weatherData.setDescription((String) weather.get("description"));
                weatherData.setIconCode((String) weather.get("icon"));
            }

            return weatherData;

        } catch (ParseException | ClassCastException e) {
            // Print the error for debugging purposes
            System.err.println("Error parsing JSON response: " + e.getMessage());
            e.printStackTrace();
            return null; // Return null to indicate a failure in parsing
        }
    }
}
