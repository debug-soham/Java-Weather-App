package com.weatherapp.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Manages all network communication with the OpenWeatherMap API.
 * This class is responsible for fetching both current weather and forecast data.
 */
public class WeatherApiClient {
    private static final String API_KEY = "YOUR_API_KEY_HERE"; // IMPORTANT: Replace with your key
    private final OkHttpClient client;

    public WeatherApiClient() {
        this.client = new OkHttpClient();
    }

    /**
     * Fetches the current weather data for a given city.
     * @param cityName The name of the city.
     * @return A JSONObject containing the current weather data, or null on failure.
     */
    public JSONObject getCurrentWeather(String cityName) {
        String safeCityName = URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8);
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + safeCityName + "&appid=" + API_KEY + "&units=metric";
        return executeApiRequest(url);
    }

    /**
     * Fetches the 5-day weather forecast for a given city.
     * @param cityName The name of the city.
     * @return A JSONObject containing the forecast data, or null on failure.
     */
    public JSONObject getFiveDayForecast(String cityName) {
        String safeCityName = URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8);
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + safeCityName + "&appid=" + API_KEY + "&units=metric";
        return executeApiRequest(url);
    }

    /**
     * Executes an HTTP GET request to the given URL and parses the JSON response.
     * @param url The URL to send the request to.
     * @return A JSONObject parsed from the response, or null if the request fails or the response is invalid.
     */
    private JSONObject executeApiRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                if (responseBody.isEmpty()) {
                    return null; // Handle empty response from the server.
                }
                // Using a new JSONParser for each request is crucial to prevent
                // parsing errors that can occur if the parser retains a failed state
                // from a previous invalid API response.
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(responseBody);
            }
        } catch (IOException | ParseException e) {
            // Log exceptions for debugging purposes.
            e.printStackTrace();
        }
        return null; // Return null on any failure.
    }
}
