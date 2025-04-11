package com.tcd.APIs;

public class TrafficDemo {
    public static void main(String[] args) {
        TrafficAPI.DataFetcher fetcher = new TrafficAPI.DataFetcher("xs68KEUJ1MMcF6w3Qg2oYKv3zkD4eo93#");

        try {
            TrafficAPI.TrafficData data = fetcher.fetch(52.41072, 4.84239);

            System.out.println("Road Class: " + data.getRoadClass());
            System.out.printf("Speed: %.1f km/h (Free Flow: %.1f km/h)\n",
                    data.getCurrentSpeed(), data.getFreeFlowSpeed());
            System.out.println("Congestion: " + data.getCongestionLevel()
                    + " (" + String.format("%.1f", data.getCongestionIndex()) + "%)");
            System.out.println("Travel Time: " + data.getCurrentTravelTime()
                    + "s (Delay: " + (data.getCurrentTravelTime() - data.getFreeFlowTravelTime()) + "s)");
            System.out.println("Data Confidence: " + data.getConfidence() + "/1");
            System.out.println("Road Closed: " + data.isRoadClosed());
            System.out.println("Coordinates Points: " + data.getCoordinates().size());
            System.out.println("API Version: " + data.getVersion());

        } catch (Exception e) {
            System.err.println("Error fetching data: " + e.getMessage());
        }
    }
}
