package com.weatherapp.api;

import com.weatherapp.model.WeatherData;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * This class is responsible for all communication with the OpenWeatherMap API.
 * It fetches the raw weather data for a given city.
 */
public class WeatherApiClient {

    // IMPORTANT: Replace this with your actual API key from OpenWeatherMap
    private static final String API_KEY = "YOUR_API_KEY_HERE";

    /**
     * Fetches weather data for a specified city.
     * @param city The name of the city.
     * @return WeatherData object containing the weather information, or null if an error occurs.
     */
    public WeatherData getWeatherData(String city) {
        // The API returns coordinates first, then we use them to get weather.
        String geoApiUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + API_KEY;

        try {
            // First, get latitude and longitude for the city
            String geoResponse = makeApiRequest(geoApiUrl);
            if (geoResponse == null) return null;

            // Parse coordinates from the response (this part is simplified for clarity)
            // A more robust implementation would use a JSON library here as well.
            // For now, we are assuming the first result is correct.
            org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
            org.json.simple.JSONArray geoArray = (org.json.simple.JSONArray) parser.parse(geoResponse);

            if (geoArray.isEmpty()) {
                System.out.println("Could not find coordinates for city: " + city);
                return null;
            }

            org.json.simple.JSONObject geoData = (org.json.simple.JSONObject) geoArray.get(0);
            double lat = (Double) geoData.get("lat");
            double lon = (Double) geoData.get("lon");

            // Now, get the weather data using the coordinates
            String weatherApiUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";

            String weatherResponse = makeApiRequest(weatherApiUrl);
            if (weatherResponse == null) return null;

            // Parse the detailed weather JSON response
            return JsonParser.parseWeatherData(weatherResponse);

        } catch (Exception e) {
            System.err.println("An error occurred during API request: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to make an HTTP GET request to a given URL.
     * @param urlString The URL to request.
     * @return The response body as a String, or null if the request fails.
     */
    private String makeApiRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            System.err.println("API Request Failed with response code: " + responseCode + " for URL: " + urlString);
            return null;
        } else {
            // Read the response body
            StringBuilder responseBody = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                responseBody.append(scanner.nextLine());
            }
            scanner.close();
            return responseBody.toString();
        }
    }
}
