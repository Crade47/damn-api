package com.tcd.APIs;

/**
 * TrafficAPI is used to provide real-time traffic flow data.
 * Fetches traffic information including speed metrics, travel times, road status,
 * and geospatial coordinates from the TomTom Traffic API for specific geographic coordinates.
 *
 * The class processes JSON responses to calculate congestion metrics and
 * returns structured traffic data through unified interfaces.
 *
 * Author: Zihan Zeng
 * @version 1.0
 */

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrafficAPI {

    // Coordinate Model
    static class Coordinate {
        private final double latitude;
        private final double longitude;

        public Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }

    // Traffic data model
    static class TrafficData {
        private final String roadClass;
        private final double currentSpeed;
        private final double freeFlowSpeed;
        private final int currentTravelTime;
        private final int freeFlowTravelTime;
        private final int confidence;
        private final boolean roadClosed;
        private final List<Coordinate> coordinates;
        private final String version;
        private final double congestionIndex;
        private final String congestionLevel;

        public TrafficData(String roadClass, double currentSpeed, double freeFlowSpeed,
                           int currentTravelTime, int freeFlowTravelTime,
                           int confidence, boolean roadClosed,
                           List<Coordinate> coordinates, String version) {
            this.roadClass = roadClass;
            this.currentSpeed = currentSpeed;
            this.freeFlowSpeed = freeFlowSpeed;
            this.currentTravelTime = currentTravelTime;
            this.freeFlowTravelTime = freeFlowTravelTime;
            this.confidence = confidence;
            this.roadClosed = roadClosed;
            this.coordinates = coordinates;
            this.version = version;

            this.congestionIndex = calculateCongestionIndex();
            this.congestionLevel = determineCongestionLevel();
        }

        private double calculateCongestionIndex() {
            return ((freeFlowSpeed - currentSpeed) / freeFlowSpeed) * 100;
        }

        private String determineCongestionLevel() {
            if (congestionIndex < 10) return "Clear";
            else if (congestionIndex < 30) return "Light Congestion";
            else if (congestionIndex < 50) return "Moderate Congestion";
            else return "Severe Congestion";
        }

        // Getters
        public String getRoadClass() { return roadClass; }
        public double getCurrentSpeed() { return currentSpeed; }
        public double getFreeFlowSpeed() { return freeFlowSpeed; }
        public int getCurrentTravelTime() { return currentTravelTime; }
        public int getFreeFlowTravelTime() { return freeFlowTravelTime; }
        public int getConfidence() { return confidence; }
        public boolean isRoadClosed() { return roadClosed; }
        public List<Coordinate> getCoordinates() { return coordinates; }
        public String getVersion() { return version; }
        public String getCongestionLevel() { return congestionLevel; }
        public double getCongestionIndex() { return congestionIndex; }
    }

    // Data Fetcher
    static class DataFetcher {
        private final String apiKey;

        public DataFetcher(String apiKey) {
            this.apiKey = apiKey;
        }

        public TrafficData fetch(double lat, double lon) throws Exception {
            String json = sendRequest(buildURL(lat, lon));
            return parseJSON(json);
        }

        private String buildURL(double lat, double lon) {
            return "https://api.tomtom.com/traffic/services/4/flowSegmentData/absolute/10/json"
                    + "?key=" + apiKey + "&point=" + lat + "," + lon;
        }

        private String sendRequest(String urlString) throws Exception {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error: " + conn.getResponseCode());
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            return response.toString();
        }

        private TrafficData parseJSON(String json) {
            JSONObject root = new JSONObject(json);
            JSONObject data = root.getJSONObject("flowSegmentData");

            return new TrafficData(
                    data.getString("frc"),
                    data.getDouble("currentSpeed"),
                    data.getDouble("freeFlowSpeed"),
                    data.getInt("currentTravelTime"),
                    data.getInt("freeFlowTravelTime"),
                    data.getInt("confidence"),
                    data.getBoolean("roadClosure"),
                    parseCoordinates(data.getJSONObject("coordinates")),
                    data.getString("@version")
            );
        }

        private List<Coordinate> parseCoordinates(JSONObject coords) {
            List<Coordinate> list = new ArrayList<>();
            JSONArray arr = coords.getJSONArray("coordinate");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject point = arr.getJSONObject(i);
                list.add(new Coordinate(
                        point.getDouble("latitude"),
                        point.getDouble("longitude")
                ));
            }
            return list;
        }
    }

}