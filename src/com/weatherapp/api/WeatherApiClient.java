package com.weatherapp.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handles all API communication with the OpenWeatherMap service.
 * This class is responsible for fetching current weather data and the 5-day forecast.
 */
public class WeatherApiClient {

    // IMPORTANT: Replace this with your actual API key from OpenWeatherMap
    private static final String API_KEY = "YOUR_API_KEY_HERE";

    private final OkHttpClient client;
    private final JSONParser parser;

    /**
     * Constructs a new WeatherApiClient, initializing the HTTP client and JSON parser.
     */
    public WeatherApiClient() {
        this.client = new OkHttpClient();
        this.parser = new JSONParser();
    }

    /**
     * Fetches the current weather data for a given city.
     * @param cityName The name of the city to get weather for.
     * @return A JSONObject containing the current weather data, or null if an error occurs.
     */
    public JSONObject getCurrentWeather(String cityName) {
        // Trim whitespace and URL-encode the city name to handle spaces and special characters
        try {
            String encodedCityName = URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8.toString());
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=" + API_KEY + "&units=metric";
            return executeRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null on encoding error
        }
    }

    /**
     * Fetches the 5-day weather forecast for a given city.
     * @param cityName The name of the city to get the forecast for.
     * @return A JSONObject containing the forecast data, or null if an error occurs.
     */
    public JSONObject getFiveDayForecast(String cityName) {
        // Trim whitespace and URL-encode the city name to handle spaces and special characters
        try {
            String encodedCityName = URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8.toString());
            String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCityName + "&appid=" + API_KEY + "&units=metric";
            return executeRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null on encoding error
        }
    }

    /**
     * Executes an HTTP GET request to the specified URL and parses the JSON response.
     * @param url The URL to make the request to.
     * @return A JSONObject from the response body, or null if an error occurs.
     */
    private JSONObject executeRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return (JSONObject) parser.parse(response.body().string());
            }
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}


