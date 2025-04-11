package com.tcd.APIs;

public class WeatherDemo {

    public static class WeatherApp {
        public static void main(String[] args) {
            String city = "Dublin";
            String country = "Ireland";
            System.out.println("Weather Condition: " + WeatherAPI.getWeatherCondition(city, country));
            System.out.println("Temperature: " + WeatherAPI.getTemperature(city, country) + "°C");
            System.out.println("Humidity: " + WeatherAPI.getHumidity(city, country) + "%");
            System.out.println("Wind Speed: " + WeatherAPI.getWindSpeed(city, country) + " km/h");
        }
    }

}
