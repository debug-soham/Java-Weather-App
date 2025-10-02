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
 * Handles all communication with the OpenWeatherMap API.
 * Fetches current weather data and the 5-day forecast.
 */
public class WeatherApiClient {
    private static final String API_KEY = "YOUR_API_KEY_HERE"; // IMPORTANT: Replace with your key
    private final OkHttpClient client;

    public WeatherApiClient() {
        this.client = new OkHttpClient();
    }

    public JSONObject getCurrentWeather(String cityName) {
        String safeCityName = URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8);
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + safeCityName + "&appid=" + API_KEY + "&units=metric";
        return executeApiRequest(url);
    }

    public JSONObject getFiveDayForecast(String cityName) {
        String safeCityName = URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8);
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + safeCityName + "&appid=" + API_KEY + "&units=metric";
        return executeApiRequest(url);
    }

    private JSONObject executeApiRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                if (responseBody.isEmpty()) {
                    return null;
                }
                // Use a new parser for each request to prevent state issues
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(responseBody);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

