package com.weatherapp.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;

/**
 * A client to communicate with the OpenWeatherMap API.
 * This class is responsible for fetching weather data from the web.
 */
public class WeatherApiClient {
    // IMPORTANT: Replace with your actual API key from OpenWeatherMap
    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private final OkHttpClient client;

    public WeatherApiClient() {
        this.client = new OkHttpClient();
    }

    /**
     * Fetches the current weather data for a given city name.
     * @param cityName The name of the city.
     * @return A JSONObject containing the current weather data, or null if an error occurs.
     */
    public JSONObject getCurrentWeather(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY + "&units=metric";
        return executeApiCall(url);
    }

    /**
     * Fetches the 5-day weather forecast for a given city name.
     * @param cityName The name of the city.
     * @return A JSONObject containing the forecast data, or null if an error occurs.
     */
    public JSONObject getFiveDayForecast(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid=" + API_KEY + "&units=metric";
        return executeApiCall(url);
    }

    /**
     * Executes the API call using OkHttp.
     * @param urlString The URL to call.
     * @return A JSONObject with the response, or null on failure.
     */
    private JSONObject executeApiCall(String urlString) {
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("API Call Unsuccessful: " + response);
                return null;
            }
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(response.body().string());
        } catch (IOException e) {
            System.err.println("IOException during API call: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            return null;
        }
    }
}

