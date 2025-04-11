package com.tcd.APIs;

/**
 * WeatherAPI is a class that provides real-time weather data.
 * It allows fetching specific weather details like temperature, humidity, wind speed,
 * and weather conditions for any city and country using WeatherAPI.com.
 *
 * Each method fetches and returns a specific piece of weather data.
 *
 * Author: Zihan Zeng
 * @version 1.0
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class WeatherAPI {
    // Replace with your WeatherAPI.com API Key
    private static final String API_KEY = "6f9322df0b874362995153638253001#"; // 5000 calls/month, use wisely

    // Fetch raw weather data
    private static JSONObject fetchWeatherData(String city, String country) throws Exception {
        String query = city;
        if (country != null && !country.isEmpty()) {
            query += "," + country;
        }

        String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=" + query + "&aqi=no";
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return new JSONObject(response.toString());
        } else {
            throw new Exception("API request failed with response code: " + responseCode);
        }
    }

    // Get weather condition (e.g., "Sunny", "Cloudy")
    public static String getWeatherCondition(String city, String country) {
        try {
            JSONObject json = fetchWeatherData(city, country);
            return json.getJSONObject("current").getJSONObject("condition").getString("text");
        } catch (Exception e) {
            return "Error fetching weather condition: " + e.getMessage();
        }
    }

    // Get current temperature in Celsius
    public static double getTemperature(String city, String country) {
        try {
            JSONObject json = fetchWeatherData(city, country);
            return json.getJSONObject("current").getDouble("temp_c");
        } catch (Exception e) {
            System.out.println("Error fetching temperature: " + e.getMessage());
            return Double.NaN;
        }
    }

    // Get humidity percentage
    public static double getHumidity(String city, String country) {
        try {
            JSONObject json = fetchWeatherData(city, country);
            return json.getJSONObject("current").getDouble("humidity");
        } catch (Exception e) {
            System.out.println("Error fetching humidity: " + e.getMessage());
            return Double.NaN;
        }
    }

    // Get wind speed in km/h
    public static double getWindSpeed(String city, String country) {
        try {
            JSONObject json = fetchWeatherData(city, country);
            return json.getJSONObject("current").getDouble("wind_kph");
        } catch (Exception e) {
            System.out.println("Error fetching wind speed: " + e.getMessage());
            return Double.NaN;
        }
    }
}
