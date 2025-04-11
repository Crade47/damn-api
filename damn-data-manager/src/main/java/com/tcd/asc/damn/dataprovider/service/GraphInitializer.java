package com.tcd.asc.damn.dataprovider.service;

import com.tcd.asc.damn.common.entity.Stop;
import com.tcd.asc.damn.common.entity.StopTime;
import com.tcd.asc.damn.common.entity.Trip;
import com.tcd.asc.damn.common.repository.StopRepository;
import com.tcd.asc.damn.common.repository.StopTimeRepository;
import com.tcd.asc.damn.common.repository.TripRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class GraphInitializer {

    @Autowired private StopRepository stopRepository;
    @Autowired private StopTimeRepository stopTimeRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private Driver neo4jDriver;

    private static final int THREAD_POOL_SIZE = 15;
    private static final double TRANSFER_WEIGHT = 5.0; // Default transfer time in minutes

    public void initializeGraph() {
        System.out.println("Initializing graph in Neo4j at service startup with " + THREAD_POOL_SIZE + " threads...");
        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (n) DETACH DELETE n"); // Clear existing graph
            buildGraphInNeo4j(session);
            System.out.println("Graph initialization in Neo4j completed.");
        } catch (Exception e) {
            System.err.println("Error initializing graph in Neo4j: " + e.getMessage());
            throw new RuntimeException("Failed to initialize transit graph in Neo4j", e);
        }
    }

    private void buildGraphInNeo4j(Session session) {
        System.out.println("Starting multi-threaded graph construction in Neo4j...");

        List<Stop> stops = stopRepository.findAll();
        System.out.println("Adding " + stops.size() + " stops as nodes in Neo4j...");
        for (Stop stop : stops) {
            session.run(
                    "CREATE (s:Stop {stopId: $stopId, stopName: $stopName, stopLat: $stopLat, stopLon: $stopLon})",
                    Map.of("stopId", stop.getStopId(), "stopName", stop.getStopName(),
                            "stopLat", stop.getStopLat(), "stopLon", stop.getStopLon())
            );
            System.out.println("Added node: " + stop.getStopName() + " (ID: " + stop.getStopId() + ")");
        }

        // Add bidirectional TRANSFER edges between Abbey Street and O'Connell - GPO, and Abbey Street and Marlborough
        addTransferEdges(session);

        List<StopTime> stopTimes = stopTimeRepository.findAll().stream().distinct().toList();
        System.out.println("Processing " + stopTimes.size() + " unique stop times...");

        // Debug: Check stop times for specific stops like Balally
        /*List<StopTime> balallyStopTimes = stopTimes.stream()
                .filter(st -> st.getStopId().equals("8250GA00291") || st.getStopId().equals("8250GA00292"))
                .collect(Collectors.toList());
        System.out.println("Stop times for Balally: " + balallyStopTimes.size());*/

        Map<String, List<StopTime>> tripStopTimesMap = stopTimes.stream()
                .collect(Collectors.groupingBy(StopTime::getTripId,
                        Collectors.mapping(st -> st, Collectors.toList())));

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<?>> futures = new ArrayList<>();

        for (Map.Entry<String, List<StopTime>> entry : tripStopTimesMap.entrySet()) {
            String tripId = entry.getKey();
            List<StopTime> tripStopTimes = entry.getValue().stream()
                    .sorted(Comparator.comparingInt(StopTime::getStopSequence))
                    .collect(Collectors.toList());
            futures.add(executorService.submit(() -> processTrip(tripId, tripStopTimes, session)));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error processing trip: " + e.getMessage());
            }
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                System.err.println("Graph population timed out.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            System.err.println("Graph population interrupted: " + e.getMessage());
        }
        System.out.println("Multi-threaded graph construction completed.");
    }

    private void addTransferEdges(Session session) {
        System.out.println("Adding TRANSFER edges between Abbey Street, O'Connell - GPO, and Marlborough...");

        // Find all stop nodes for Abbey Street, O'Connell - GPO, and Marlborough
        Result abbeyStreetResult = session.run("MATCH (s:Stop {stopName: 'Abbey Street'}) RETURN s.stopId AS stopId");
        Result oconnellGpoResult = session.run("MATCH (s:Stop {stopName: \"O'Connell - GPO\"}) RETURN s.stopId AS stopId");
        Result marlboroughResult = session.run("MATCH (s:Stop {stopName: 'Marlborough'}) RETURN s.stopId AS stopId");

        List<String> abbeyStreetStopIds = abbeyStreetResult.stream().map(record -> record.get("stopId").asString()).collect(Collectors.toList());
        List<String> oconnellGpoStopIds = oconnellGpoResult.stream().map(record -> record.get("stopId").asString()).collect(Collectors.toList());
        List<String> marlboroughStopIds = marlboroughResult.stream().map(record -> record.get("stopId").asString()).collect(Collectors.toList());

        if (abbeyStreetStopIds.isEmpty() || oconnellGpoStopIds.isEmpty() || marlboroughStopIds.isEmpty()) {
            System.err.println("Warning: One or more transfer stops not found. Abbey Street: " + abbeyStreetStopIds.size() +
                    ", O'Connell - GPO: " + oconnellGpoStopIds.size() + ", Marlborough: " + marlboroughStopIds.size());
            return;
        }

        // Create bidirectional TRANSFER edges between Abbey Street and O'Connell - GPO
        for (String abbeyId : abbeyStreetStopIds) {
            for (String oconnellId : oconnellGpoStopIds) {
                if (!abbeyId.equals(oconnellId)) { // Avoid self-loops
                    session.run(
                            "MATCH (from:Stop {stopId: $fromStopId}), (to:Stop {stopId: $toStopId}) " +
                                    "MERGE (from)-[r:TRANSFER]->(to) " +
                                    "SET r.weight = $weight, " +
                                    "r.created = COALESCE(r.created, timestamp()), " +
                                    "r.updated = timestamp()",
                            Map.of("fromStopId", abbeyId, "toStopId", oconnellId, "weight", TRANSFER_WEIGHT)
                    );
                    session.run(
                            "MATCH (from:Stop {stopId: $fromStopId}), (to:Stop {stopId: $toStopId}) " +
                                    "MERGE (from)-[r:TRANSFER]->(to) " +
                                    "SET r.weight = $weight, " +
                                    "r.created = COALESCE(r.created, timestamp()), " +
                                    "r.updated = timestamp()",
                            Map.of("fromStopId", oconnellId, "toStopId", abbeyId, "weight", TRANSFER_WEIGHT)
                    );
                    System.out.println("Added TRANSFER edge: Abbey Street (" + abbeyId + ") <-> O'Connell - GPO (" + oconnellId + ") with weight " + TRANSFER_WEIGHT);
                }
            }
        }

        // Create bidirectional TRANSFER edges between Abbey Street and Marlborough
        for (String abbeyId : abbeyStreetStopIds) {
            for (String marlboroughId : marlboroughStopIds) {
                if (!abbeyId.equals(marlboroughId)) { // Avoid self-loops
                    session.run(
                            "MATCH (from:Stop {stopId: $fromStopId}), (to:Stop {stopId: $toStopId}) " +
                                    "MERGE (from)-[r:TRANSFER]->(to) " +
                                    "SET r.weight = $weight, " +
                                    "r.created = COALESCE(r.created, timestamp()), " +
                                    "r.updated = timestamp()",
                            Map.of("fromStopId", abbeyId, "toStopId", marlboroughId, "weight", TRANSFER_WEIGHT)
                    );
                    session.run(
                            "MATCH (from:Stop {stopId: $fromStopId}), (to:Stop {stopId: $toStopId}) " +
                                    "MERGE (from)-[r:TRANSFER]->(to) " +
                                    "SET r.weight = $weight, " +
                                    "r.created = COALESCE(r.created, timestamp()), " +
                                    "r.updated = timestamp()",
                            Map.of("fromStopId", marlboroughId, "toStopId", abbeyId, "weight", TRANSFER_WEIGHT)
                    );
                    System.out.println("Added TRANSFER edge: Abbey Street (" + abbeyId + ") <-> Marlborough (" + marlboroughId + ") with weight " + TRANSFER_WEIGHT);
                }
            }
        }

        for (String oconnellId : oconnellGpoStopIds) {
            for (String marlboroughId : marlboroughStopIds) {
                if (!oconnellId.equals(marlboroughId)) { // Avoid self-loops
                    session.run(
                            "MATCH (from:Stop {stopId: $fromStopId}), (to:Stop {stopId: $toStopId}) " +
                                    "MERGE (from)-[r:TRANSFER]->(to) " +
                                    "SET r.weight = $weight, " +
                                    "r.created = COALESCE(r.created, timestamp()), " +
                                    "r.updated = timestamp()",
                            Map.of("fromStopId", oconnellId, "toStopId", marlboroughId, "weight", TRANSFER_WEIGHT)
                    );
                    session.run(
                            "MATCH (from:Stop {stopId: $fromStopId}), (to:Stop {stopId: $toStopId}) " +
                                    "MERGE (from)-[r:TRANSFER]->(to) " +
                                    "SET r.weight = $weight, " +
                                    "r.created = COALESCE(r.created, timestamp()), " +
                                    "r.updated = timestamp()",
                            Map.of("fromStopId", marlboroughId, "toStopId", oconnellId, "weight", TRANSFER_WEIGHT)
                    );
                    System.out.println("Added TRANSFER edge: Abbey Street (" + oconnellId + ") <-> Marlborough (" + marlboroughId + ") with weight " + TRANSFER_WEIGHT);
                }
            }
        }
    }

    private void processTrip(String tripId, List<StopTime> tripStopTimes, Session session) {
        System.out.println("Processing trip " + tripId + " with " + tripStopTimes.size() + " stops...");
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found: " + tripId));

        // Get the route associated with the trip and extract routeShortName
        String routeShortName = trip.getRoute() != null ? trip.getRoute().getRouteShortName().toUpperCase(Locale.ROOT) : null;
        if (routeShortName == null || routeShortName.isEmpty()) {
            System.err.println("Warning: routeShortName is null or empty for trip " + tripId + ". Using default 'UNKNOWN_LUAS'.");
            routeShortName = "UNKNOWN";
        }

        // Construct the dynamic edge type (e.g., GREEN_LUAS or RED_LUAS)
        String edgeType = routeShortName + "_LUAS";

        Integer directionId = trip.getDirectionId();
        if (directionId == null) {
            System.err.println("Warning: directionId is null for trip " + tripId + ". Using default 0.");
            directionId = 0;
        }

        for (int i = 0; i < tripStopTimes.size() - 1; i++) {
            StopTime current = tripStopTimes.get(i);
            StopTime next = tripStopTimes.get(i + 1);
            double weight = calculateEdgeWeight(current, next);
            if (weight <= 0) {
                weight = 1.0;
                System.err.println("Invalid weight calculated for " + current.getStop().getStopName() + " -> " + next.getStop().getStopName() + ". Using default 1.0");
            }

            // Dynamically construct the Cypher query with the edge type
            String cypherQuery = String.format(
                    "MATCH (from:Stop {stopId: $fromStopId}), (to:Stop {stopId: $toStopId}) " +
                            "MERGE (from)-[r:%s]->(to) " +
                            "SET r.weight = $weight, " +
                            "r.directionId = $directionId, " +
                            "r.tripIds = CASE WHEN r.tripIds IS NULL THEN [$tripId] ELSE r.tripIds + [$tripId] END, " +
                            "r.created = COALESCE(r.created, timestamp()), " +
                            "r.updated = timestamp()",
                    edgeType
            );

            session.run(
                    cypherQuery,
                    Map.of(
                            "fromStopId", current.getStopId(),
                            "toStopId", next.getStopId(),
                            "weight", weight,
                            "directionId", directionId,
                            "tripId", tripId
                    )
            );
            System.out.println("Added or updated relationship: " + current.getStop().getStopName() + " -> " +
                    next.getStop().getStopName() + " (type: " + edgeType + ", weight: " + weight + ", directionId: " + directionId + ", tripId: " + tripId + ")");
        }
    }

    private double calculateEdgeWeight(StopTime from, StopTime to) {
        if (from.getDepartureTime() != null && to.getArrivalTime() != null) {
            long minutes = java.time.temporal.ChronoUnit.MINUTES.between(from.getDepartureTime(), to.getArrivalTime());
            return minutes > 0 ? minutes : 1.0;
        }
        Stop fromStop = from.getStop();
        Stop toStop = to.getStop();
        return haversineDistance(fromStop.getStopLat(), fromStop.getStopLon(),
                toStop.getStopLat(), toStop.getStopLon());
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}