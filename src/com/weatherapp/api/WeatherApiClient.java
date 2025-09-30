package com.weatherapp.api;

import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.List;

/**
 * Handles communication with the OpenWeatherMap API.
 * This class is responsible for making HTTP requests to fetch weather and forecast data.
 */
public class WeatherApiClient {

    private static final String API_KEY = "YOUR_API_KEY_HERE"; // Remember to replace this
    private final OkHttpClient client;

    public WeatherApiClient() {
        this.client = new OkHttpClient();
    }

    /**
     * Fetches the current weather data for a given city.
     * @param city The name of the city.
     * @return A WeatherData object, or null if an error occurs.
     */
    public WeatherData getWeatherData(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
        try {
            String json = makeApiCall(url);
            if (json != null) {
                return JsonParser.parseWeatherData(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetches the 5-day weather forecast for a given city.
     * @param city The name of the city.
     * @return A list of ForecastData objects, or null if an error occurs.
     */
    public List<ForecastData> getForecastData(String city) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + API_KEY + "&units=metric";
        try {
            String json = makeApiCall(url);
            if (json != null) {
                return JsonParser.parseForecastData(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A generic method to make an API call using OkHttp.
     * @param url The URL for the API request.
     * @return The JSON response as a string, or null on failure.
     * @throws IOException if a network error occurs.
     */
    private String makeApiCall(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        }
        return null;
    }
}

