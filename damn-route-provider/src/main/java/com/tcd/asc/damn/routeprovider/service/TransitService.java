package com.tcd.asc.damn.routeprovider.service;

import com.tcd.asc.damn.common.constants.TransitType;
import com.tcd.asc.damn.common.entity.Shape;
import com.tcd.asc.damn.common.entity.Stop;
import com.tcd.asc.damn.common.entity.Trip;
import com.tcd.asc.damn.common.model.dto.Coordinates;
import com.tcd.asc.damn.common.model.dto.RouteSegment;
import com.tcd.asc.damn.common.model.dto.TransitSegment;
import com.tcd.asc.damn.common.model.dto.WalkSegment;
import com.tcd.asc.damn.common.model.request.RouteRequest;
import com.tcd.asc.damn.common.model.response.RouteResponse;
import com.tcd.asc.damn.common.model.response.RoutesResponse;
import com.tcd.asc.damn.common.repository.ShapeRepository;
import com.tcd.asc.damn.common.repository.StopRepository;
import com.tcd.asc.damn.common.repository.StopTimeRepository;
import com.tcd.asc.damn.common.repository.TripRepository;
import com.tcd.asc.damn.routeprovider.utils.AlphanumericGenerator;
import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransitService {

    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private StopTimeRepository stopTimeRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private ShapeRepository shapeRepository;
    @Autowired
    private Driver neo4jDriver;
    @Autowired
    private AlphanumericGenerator alphanumericGenerator;

    private static final int NEAREST_STOPS_LIMIT = 3;
    private static final double TRANSFER_PENALTY = 5.0;

    public RoutesResponse findRoutes(RouteRequest routeRequest) {
        double startLat = routeRequest.getStartLocation().getLatitude();
        double startLon = routeRequest.getStartLocation().getLongitude();
        double endLat = routeRequest.getEndLocation().getLatitude();
        double endLon = routeRequest.getEndLocation().getLongitude();

        System.out.println("Finding routes from (" + startLat + ", " + startLon + ") to (" + endLat + ", " + endLon + ")");

        try (Session session = neo4jDriver.session()) {
            List<Stop> startStops = findNearestStops(startLat, startLon, NEAREST_STOPS_LIMIT);
            System.out.println("Nearest start stops:");
            startStops.forEach(stop -> System.out.println(" - " + stop.getStopName() + " (ID: " + stop.getStopId() + ")"));

            List<Stop> endStops = findNearestStops(endLat, endLon, NEAREST_STOPS_LIMIT);
            System.out.println("Nearest end stops:");
            endStops.forEach(stop -> System.out.println(" - " + stop.getStopName() + " (ID: " + stop.getStopId() + ")"));

            RoutesResponse routesResponse = new RoutesResponse();
            List<RouteResponse> routeResponses = new ArrayList<>();

            StringBuilder errorDetails = new StringBuilder();
            for (Stop startStop : startStops) {
                for (Stop endStop : endStops) {
                    List<RouteSegment> segments = findRouteBetweenStops(startStop, endStop, session, errorDetails);
                    if (segments != null && !segments.isEmpty()) {
                        RouteResponse routeResponse = new RouteResponse();
                        routeResponse.setRouteId(alphanumericGenerator.generateAlphanumericString());
                        // Add initial walk segment from start location to boarding stop
                        List<RouteSegment> routeSegments = new ArrayList<>();

                        WalkSegment walkSegment = new WalkSegment();
                        walkSegment.setStartCoordinate(routeRequest.getStartLocation());
                        walkSegment.setEndCoordinate(new Coordinates(startStop.getStopLat(), startStop.getStopLon()));
                        walkSegment.setWalkPath(null);
                        routeSegments.add(walkSegment);

                        // Add the transit and walk segments
                        routeSegments.addAll(segments);

                        // Add final walk segment from alighting stop to end location
                        walkSegment.setStartCoordinate(new Coordinates(endStop.getStopLat(), endStop.getStopLon()));
                        walkSegment.setEndCoordinate(routeRequest.getEndLocation());
                        walkSegment.setWalkPath(null);
                        routeSegments.add(walkSegment);

                        routeResponse.setRoutes(routeSegments);
                        routeResponse.setUniqueTransitTypes(routeSegments.stream().map(RouteSegment::getTransitType).distinct().toList());
                        routeResponses.add(routeResponse);
                    }
                }
            }

            if (routeResponses.isEmpty()) {
                String errorMessage = "No routes found between any start and end stops. " +
                        "Start stops: " + startStops.stream().map(s -> s.getStopName() + " (" + s.getStopId() + ")").collect(Collectors.joining(", ")) +
                        ". End stops: " + endStops.stream().map(s -> s.getStopName() + " (" + s.getStopId() + ")").collect(Collectors.joining(", ")) +
                        ". " + errorDetails.toString();
                System.out.println(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            System.out.println("Found " + routeResponses.size() + " possible routes.");
            return new RoutesResponse(routeRequest.getStartLocation(), routeRequest.getEndLocation(),routeResponses.size(), routeResponses);
        } catch (Exception e) {
            System.err.println("Error finding routes: " + e.getMessage());
            throw new RuntimeException("Failed to find routes: " + e.getMessage(), e);
        }
    }

    private List<Stop> findNearestStops(double lat, double lon, int limit) {
        List<Stop> allStops = stopRepository.findAll().stream()
                .toList();

        return allStops.stream()
                .sorted(Comparator.comparingDouble(s -> haversineDistance(lat, lon, s.getStopLat(), s.getStopLon())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<RouteSegment> findRouteBetweenStops(Stop startStop, Stop endStop, Session session, StringBuilder errorDetails) {
        // Use apoc.algo.aStar to find the shortest path with RED_LUAS, TRANSFER, or GREEN_LUAS relationships
        Result result = session.run(
                "MATCH (start:Stop {stopId: $startStopId}), (end:Stop {stopId: $endStopId}) " +
                        "CALL apoc.algo.aStar(" +
                        "  start, " +
                        "  end, " +
                        "  'RED_LUAS>|GREEN_LUAS>|TRANSFER>', " +
                        "  'weight', " +
                        "  'stopLat', " +
                        "  'stopLon'" +
                        ") YIELD path, weight " +
                        "RETURN [node IN nodes(path) | {stopId: node.stopId, stopName: node.stopName, " +
                        "stopLat: node.stopLat, stopLon: node.stopLon}] AS stopPath, " +
                        "relationships(path) AS rawRelationships, " +
                        "weight AS totalWeight",
                Map.of("startStopId", startStop.getStopId(), "endStopId", endStop.getStopId())
        );

        if (!result.hasNext()) {
            String debugInfo = String.format(
                    "No path found between %s (ID: %s) and %s (ID: %s). " +
                            "Possible reasons: Graph might be disconnected or search depth exceeded.",
                    startStop.getStopName(), startStop.getStopId(),
                    endStop.getStopName(), endStop.getStopId()
            );
            System.out.println(debugInfo);
            errorDetails.append(debugInfo).append("\n");
            return null;
        }

        var record = result.single();
        List<Object> rawStopPath = record.get("stopPath").asList();
        List<Value> rawRelationships = record.get("rawRelationships").asList(Values::value);
        double totalWeight = record.get("totalWeight").asDouble();

        // Convert raw path to Stop objects
        List<Stop> stopPath = rawStopPath.stream()
                .map(obj -> {
                    Map<String, Object> node = (Map<String, Object>) obj;
                    return stopRepository.findById((String) node.get("stopId")).get();
                })
                .collect(Collectors.toList());

        // Verify no loops
        Set<String> visitedStopIds = new HashSet<>();
        for (Stop stop : stopPath) {
            if (!visitedStopIds.add(stop.getStopId())) {
                System.out.println("Loop detected in route at " + stop.getStopName() + " (ID: " + stop.getStopId() + "). Path discarded.");
                return null;
            }
        }

        // Process relationships to determine path details and segment the route
        List<String> relationshipTypes = new ArrayList<>();
        List<List<String>> segmentTripIds = new ArrayList<>();
        List<Double> segmentWeights = new ArrayList<>(); // Track weights for each segment
        List<String> currentSegmentTripIds = new ArrayList<>();
        double currentSegmentWeight = 0.0;
        for (Value relValue : rawRelationships) {
            String relType = relValue.asRelationship().type();
            relationshipTypes.add(relType);
            Map<String, Object> relProps = relValue.asMap();
            double weight = relProps.containsKey("weight") ? ((Number) relProps.get("weight")).doubleValue() : 1.0;
            if (relType.equals("TRANSFER")) {
                totalWeight += TRANSFER_PENALTY; // Apply transfer penalty to total
                currentSegmentWeight += weight; // Include TRANSFER weight in the current segment
                // Save the current segment's trip IDs and weight, then start a new segment
                segmentTripIds.add(new ArrayList<>(currentSegmentTripIds));
                segmentWeights.add(currentSegmentWeight);
                currentSegmentTripIds.clear();
                currentSegmentWeight = 0.0;
            } else {
                currentSegmentWeight += weight;
                if (relProps.containsKey("tripIds")) {
                    Object tripIdsObj = relProps.get("tripIds");
                    if (tripIdsObj instanceof List) {
                        currentSegmentTripIds.addAll((List<String>) tripIdsObj);
                    } else if (tripIdsObj instanceof String) {
                        currentSegmentTripIds.add((String) tripIdsObj);
                    }
                }
            }
        }
        // Add the last segment's trip IDs and weight
        segmentTripIds.add(currentSegmentTripIds);
        segmentWeights.add(currentSegmentWeight);

        // Segment the path based on TRANSFER relationships
        List<RouteSegment> segments = new ArrayList<>();
        List<Integer> transferIndices = new ArrayList<>();
        for (int i = 0; i < relationshipTypes.size(); i++) {
            if (relationshipTypes.get(i).equals("TRANSFER")) {
                transferIndices.add(i);
            }
        }

        transferIndices.add(relationshipTypes.size()); // Add the end index to process the last segment
        int startIndex = 0;
        int segmentIndex = 0;

        for (int transferIndex : transferIndices) {
            // Define the segment of stops up to the transfer (or end)
            List<Stop> segmentStops = stopPath.subList(startIndex, transferIndex + 1);
            if (!segmentStops.isEmpty()) {
                Stop segmentStart = segmentStops.get(0);
                Stop segmentEnd = segmentStops.get(segmentStops.size() - 1);

                // Build transit path for this segment using the trip IDs for this segment
                List<Coordinates> transitPath = new ArrayList<>();
                List<String> tripIdsForSegment = segmentTripIds.get(segmentIndex).stream().distinct().collect(Collectors.toList());
                if (!tripIdsForSegment.isEmpty()) {
                    String selectedTripId = tripIdsForSegment.get(0); // Use the first trip ID for this segment
                    Trip trip = tripRepository.findById(selectedTripId)
                            .orElseThrow(() -> new RuntimeException("Trip not found: " + selectedTripId));
                    String shapeId = trip.getShapeId();
                    if (shapeId != null) {
                        List<Shape> shapes = shapeRepository.findByShapeId(shapeId);
                        transitPath = shapes.stream()
                                .sorted(Comparator.comparingInt(Shape::getShapePtSequence))
                                .map(shape -> new Coordinates(shape.getShapePtLat(), shape.getShapePtLon()))
                                .collect(Collectors.toList());
                    } else {
                        System.out.println("No shapeId found for trip: " + selectedTripId);
                    }
                }

                List<Coordinates> filteredTransitPath = filterTransitPathByStops(segmentStops, transitPath);
                TransitSegment transitSegment = new TransitSegment();
                transitSegment.setBoardingStop(segmentStart);
                transitSegment.setAlightingStop(segmentEnd);
                transitSegment.setStopPath(segmentStops);
                transitSegment.setTransitPath(filteredTransitPath);
                transitSegment.setTransitType(TransitType.LUAS);
                // Calculate travelDistance based on transitPath (optional)
                double travelDistance = calculateTravelDistance(filteredTransitPath);
                transitSegment.setTravelDistance(travelDistance);
                // Estimate travelTime based on segment weight (assuming weight correlates with time)
                double travelTime = segmentWeights.get(segmentIndex) * 60; // Convert to seconds (arbitrary factor)
                transitSegment.setTravelTime(travelTime);
                // Set travelCost (arbitrary for now, can be based on distance or a fixed fare)
                transitSegment.setTravelCost(travelDistance * 0.1); // Example: 0.1 currency unit per km

                // Wrap the TransitRoute in a TransitSegment
                //TransitSegment transitSegment = new TransitSegment();
                //transitSegment.setTransitRoute(Collections.singletonList(transitRoute));
                segments.add(transitSegment);

                // Add WalkSegment if this is not the last segment
                if (transferIndex < relationshipTypes.size()) {
                    Coordinates fromCoord = new Coordinates(segmentEnd.getStopLat(), segmentEnd.getStopLon());
                    Stop nextSegmentStart = stopPath.get(transferIndex + 1);
                    Coordinates toCoord = new Coordinates(nextSegmentStart.getStopLat(), nextSegmentStart.getStopLon());
                    WalkSegment walkSegment = new WalkSegment();
                    walkSegment.setStartCoordinate(fromCoord);
                    walkSegment.setEndCoordinate(toCoord);
                    walkSegment.setWalkPath(null);
                    segments.add(walkSegment);
                }
            }
            startIndex = transferIndex + 1;
            segmentIndex++;
        }

        // If no transfers, return a single TransitSegment
        if (transferIndices.size() == 1) {
            List<Coordinates> transitPath = new ArrayList<>();
            List<String> allTripIds = segmentTripIds.get(0).stream().distinct().collect(Collectors.toList());
            if (!allTripIds.isEmpty()) {
                String selectedTripId = allTripIds.get(0);
                Trip trip = tripRepository.findById(selectedTripId)
                        .orElseThrow(() -> new RuntimeException("Trip not found: " + selectedTripId));
                String shapeId = trip.getShapeId();
                if (shapeId != null) {
                    List<Shape> shapes = shapeRepository.findByShapeId(shapeId);
                    transitPath = shapes.stream()
                            .sorted(Comparator.comparingInt(Shape::getShapePtSequence))
                            .map(shape -> new Coordinates(shape.getShapePtLat(), shape.getShapePtLon()))
                            .collect(Collectors.toList());
                } else {
                    System.out.println("No shapeId found for trip: " + selectedTripId);
                }
            }

            List<Coordinates> filteredTransitPath = filterTransitPathByStops(stopPath, transitPath);
            TransitSegment transitSegment = new TransitSegment();
            transitSegment.setBoardingStop(startStop);
            transitSegment.setAlightingStop(endStop);
            transitSegment.setStopPath(stopPath);
            transitSegment.setTransitPath(filteredTransitPath);
            transitSegment.setTransitType(TransitType.LUAS);
            // Calculate travelDistance, travelTime, and travelCost
            double travelDistance = calculateTravelDistance(filteredTransitPath);
            transitSegment.setTravelDistance(travelDistance);
            double travelTime = segmentWeights.get(0) * 60; // Convert to seconds
            transitSegment.setTravelTime(travelTime);
            transitSegment.setTravelCost(travelDistance * 0.1);

            //TransitSegment transitSegment = new TransitSegment();
            //transitSegment.setTransitRoute(Collections.singletonList(transitSegment));
            segments.add(transitSegment);
        }

        String stopsString = stopPath.stream()
                .map(stop -> stop.getStopName() + " (" + stop.getStopId() + ")")
                .collect(Collectors.joining("->"));

        System.out.println("Route found: " + stopsString +
                " with " + stopPath.size() + " stops, total weight: " + totalWeight);

        return segments;
    }

    private double calculateTravelDistance(List<Coordinates> path) {
        if (path == null || path.size() < 2) return 0.0;
        double distance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Coordinates c1 = path.get(i);
            Coordinates c2 = path.get(i + 1);
            distance += haversineDistance(c1.getLatitude(), c1.getLongitude(), c2.getLatitude(), c2.getLongitude());
        }
        return distance;
    }

    private List<Coordinates> filterTransitPathByStops(List<Stop> stopPath, List<Coordinates> transitPath) {
        if (stopPath.isEmpty() || transitPath.isEmpty()) {
            return new ArrayList<>();
        }

        List<Coordinates> filteredPath = new ArrayList<>();
        Map<Stop, Coordinates> stopToNearestCoord = new HashMap<>();

        for (Stop stop : stopPath) {
            Coordinates nearestCoord = findNearestCoordinate(stop, transitPath);
            if (nearestCoord != null) {
                stopToNearestCoord.put(stop, nearestCoord);
            }
        }

        List<Coordinates> sortedTransitPath = new ArrayList<>(transitPath);
        sortedTransitPath.sort((c1, c2) -> {
            int index1 = transitPath.indexOf(c1);
            int index2 = transitPath.indexOf(c2);
            return Integer.compare(index1, index2);
        });

        List<Integer> stopIndices = stopToNearestCoord.values().stream()
                .map(sortedTransitPath::indexOf)
                .filter(index -> index >= 0)
                .sorted()
                .collect(Collectors.toList());

        if (stopIndices.isEmpty()) {
            return new ArrayList<>();
        }

        int startIndex = stopIndices.get(0);
        int endIndex = stopIndices.get(stopIndices.size() - 1);
        if (startIndex <= endIndex && endIndex < sortedTransitPath.size()) {
            filteredPath.addAll(sortedTransitPath.subList(startIndex, endIndex + 1));
        }

        return filteredPath;
    }

    private Coordinates findNearestCoordinate(Stop stop, List<Coordinates> coordinates) {
        if (coordinates.isEmpty()) return null;

        double minDistance = Double.MAX_VALUE;
        Coordinates nearest = null;

        for (Coordinates coord : coordinates) {
            double distance = haversineDistance(stop.getStopLat(), stop.getStopLon(),
                    coord.getLatitude(), coord.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = coord;
            }
        }

        if (minDistance > 0.1) {
            //System.out.println("Warning: Nearest coordinate for stop " + stop.getStopName() + " is " + minDistance + " km away, which may be inaccurate.");
            return null;
        }

        return nearest;
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