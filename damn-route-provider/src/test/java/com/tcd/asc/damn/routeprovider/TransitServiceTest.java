package com.tcd.asc.damn.routeprovider;

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
import com.tcd.asc.damn.routeprovider.service.TransitService;
import com.tcd.asc.damn.routeprovider.utils.AlphanumericGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Relationship;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransitServiceTest {

    @InjectMocks
    private TransitService transitService;

    @Mock
    private StopRepository stopRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private ShapeRepository shapeRepository;

    @Mock
    private StopTimeRepository stopTimeRepository;

    @Mock
    private Driver neo4jDriver;

    @Mock
    private Session session;

    @Mock
    private AlphanumericGenerator alphanumericGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(transitService, "neo4jDriver", neo4jDriver);
    }

    //@Test
    @DisplayName("SUCCESSFUL_ROUTE_WITH_SINGLE_TRANSIT")
    void successfulRouteWithSingleTransit() {
        // Arrange
        when(neo4jDriver.session()).thenReturn(session);
        RouteRequest request = new RouteRequest();
        request.setStartLocation(new Coordinates(53.34, -6.26));
        request.setEndLocation(new Coordinates(53.35, -6.25));

        Stop stop1 = new Stop(); stop1.setStopId("1"); stop1.setStopName("Stop1"); stop1.setStopLat(53.341); stop1.setStopLon(-6.261);
        Stop stop2 = new Stop(); stop2.setStopId("2"); stop2.setStopName("Stop2"); stop2.setStopLat(53.351); stop2.setStopLon(-6.251);
        List<Stop> stops = Arrays.asList(stop1, stop2);
        when(stopRepository.findAll()).thenReturn(stops);

        Result result = mock(Result.class);
        when(result.hasNext()).thenReturn(true);
        Record record = mock(Record.class);
        when(result.single()).thenReturn(record);

        Value stopPathValue = mock(Value.class);
        when(record.get("stopPath")).thenReturn(stopPathValue);
        List<Map<String, Object>> stopPathList = List.of(
                Map.of("stopId", "1", "stopName", "Stop1", "stopLat", 53.341, "stopLon", -6.261),
                Map.of("stopId", "2", "stopName", "Stop2", "stopLat", 53.351, "stopLon", -6.251)
        );
        doReturn(stopPathList).when(stopPathValue).asList();

        Value rel = mock(Value.class);
        Relationship relationship = mock(Relationship.class);
        when(rel.asRelationship()).thenReturn(relationship);
        when(relationship.type()).thenReturn("RED_LUAS");
        when(rel.asMap()).thenReturn(Map.of("tripIds", List.of("T1"), "weight", 5.0));
        Value rawRelationshipsValue = mock(Value.class);
        when(record.get("rawRelationships")).thenReturn(rawRelationshipsValue);
        // Fix: Stub asList with the correct argument
        doReturn(List.of(rel)).when(rawRelationshipsValue).asList(Values::value);
        when(record.get("totalWeight")).thenReturn(Values.value(10.0));
        when(session.run(anyString(), anyMap())).thenReturn(result);

        when(stopRepository.findById("1")).thenReturn(Optional.of(stop1));
        when(stopRepository.findById("2")).thenReturn(Optional.of(stop2));
        Trip trip = new Trip(); trip.setTripId("T1"); trip.setShapeId("S1");
        when(tripRepository.findById("T1")).thenReturn(Optional.of(trip));
        Shape shape = new Shape(); shape.setShapeId("S1"); shape.setShapePtLat(53.345); shape.setShapePtLon(-6.255); shape.setShapePtSequence(1);
        List<Shape> shapes = List.of(shape);
        when(shapeRepository.findByShapeId("S1")).thenReturn(shapes);
        when(alphanumericGenerator.generateAlphanumericString()).thenReturn("ROUTE123");

        /*// Act
        RoutesResponse response = transitService.findRoutes(request);

        // Assert
        assertEquals(1, response.getNoOfRoutes());
        RouteResponse route = response.getRouteResponses().get(0);
        assertEquals("ROUTE123", route.getRouteId());
        assertEquals(3, route.getRoutes().size()); // Walk -> Transit -> Walk
        assertTrue(route.getRoutes().get(0) instanceof WalkSegment);
        assertTrue(route.getRoutes().get(1) instanceof TransitSegment);
        assertTrue(route.getRoutes().get(2) instanceof WalkSegment);*/
    }

    @Test
    @DisplayName("NO_ROUTES_FOUND")
    public void noRoutesFound() {
        // Arrange
        when(neo4jDriver.session()).thenReturn(session);
        RouteRequest request = new RouteRequest();
        request.setStartLocation(new Coordinates(53.34, -6.26));
        request.setEndLocation(new Coordinates(53.35, -6.25));

        Stop stop1 = new Stop(); stop1.setStopId("1"); stop1.setStopName("Stop1"); stop1.setStopLat(53.341); stop1.setStopLon(-6.261);
        Stop stop2 = new Stop(); stop2.setStopId("2"); stop2.setStopName("Stop2"); stop2.setStopLat(53.351); stop2.setStopLon(-6.251);
        List<Stop> stops = Arrays.asList(stop1, stop2);
        when(stopRepository.findAll()).thenReturn(stops);

        Result result = mock(Result.class);
        when(result.hasNext()).thenReturn(false);
        when(session.run(anyString(), anyMap())).thenReturn(result);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> transitService.findRoutes(request));
        assertTrue(exception.getMessage().contains("No routes found"));
    }

    //@Test
    @DisplayName("ROUTE_WITH_TRANSFER")
    public void routeWithTransfer() {
        // Arrange
        when(neo4jDriver.session()).thenReturn(session);
        RouteRequest request = new RouteRequest();
        request.setStartLocation(new Coordinates(53.34, -6.26));
        request.setEndLocation(new Coordinates(53.36, -6.24));

        Stop stop1 = new Stop(); stop1.setStopId("1"); stop1.setStopName("Stop1"); stop1.setStopLat(53.341); stop1.setStopLon(-6.261);
        Stop stop2 = new Stop(); stop2.setStopId("2"); stop2.setStopName("Stop2"); stop2.setStopLat(53.351); stop2.setStopLon(-6.251);
        Stop stop3 = new Stop(); stop3.setStopId("3"); stop3.setStopName("Stop3"); stop3.setStopLat(53.361); stop3.setStopLon(-6.241);
        List<Stop> stops = Arrays.asList(stop1, stop2, stop3);
        when(stopRepository.findAll()).thenReturn(stops);

        Result result = mock(Result.class);
        when(result.hasNext()).thenReturn(true);
        Record record = mock(Record.class);
        when(result.single()).thenReturn(record);

        Value stopPathValue = mock(Value.class);
        when(record.get("stopPath")).thenReturn(stopPathValue);
        List<Map<String, Object>> stopPathList = List.of(
                Map.of("stopId", "1", "stopName", "Stop1", "stopLat", 53.341, "stopLon", -6.261),
                Map.of("stopId", "2", "stopName", "Stop2", "stopLat", 53.351, "stopLon", -6.251),
                Map.of("stopId", "3", "stopName", "Stop3", "stopLat", 53.361, "stopLon", -6.241)
        );
        doReturn(stopPathList).when(stopPathValue).asList();

        Value rel1 = mock(Value.class);
        Relationship relationship1 = mock(Relationship.class);
        when(rel1.asRelationship()).thenReturn(relationship1);
        when(relationship1.type()).thenReturn("RED_LUAS");
        when(rel1.asMap()).thenReturn(Map.of("tripIds", List.of("T1"), "weight", 5.0));

        Value rel2 = mock(Value.class);
        Relationship relationship2 = mock(Relationship.class);
        when(rel2.asRelationship()).thenReturn(relationship2);
        when(relationship2.type()).thenReturn("TRANSFER");
        when(rel2.asMap()).thenReturn(Map.of("weight", 2.0));

        Value rawRelationshipsValue = mock(Value.class);
        when(record.get("rawRelationships")).thenReturn(rawRelationshipsValue);
        doReturn(List.of(rel1, rel2)).when(rawRelationshipsValue).asList();
        when(record.get("totalWeight")).thenReturn(Values.value(12.0));
        when(session.run(anyString(), anyMap())).thenReturn(result);

        when(stopRepository.findById("1")).thenReturn(Optional.of(stop1));
        when(stopRepository.findById("2")).thenReturn(Optional.of(stop2));
        when(stopRepository.findById("3")).thenReturn(Optional.of(stop3));
        Trip trip = new Trip(); trip.setTripId("T1"); trip.setShapeId("S1");
        when(tripRepository.findById("T1")).thenReturn(Optional.of(trip));
        Shape shape = new Shape(); shape.setShapeId("S1"); shape.setShapePtLat(53.345); shape.setShapePtLon(-6.255); shape.setShapePtSequence(1);
        List<Shape> shapes = List.of(shape);
        when(shapeRepository.findByShapeId("S1")).thenReturn(shapes);
        when(alphanumericGenerator.generateAlphanumericString()).thenReturn("ROUTE456");

        /*// Act
        RoutesResponse response = transitService.findRoutes(request);

        // Assert
        assertEquals(1, response.getNoOfRoutes());
        List<RouteSegment> segments = response.getRouteResponses().get(0).getRoutes();
        assertEquals(4, segments.size()); // Walk -> Transit -> Walk (transfer) -> Transit
        assertTrue(segments.get(1) instanceof TransitSegment);
        assertTrue(segments.get(2) instanceof WalkSegment);*/
    }

    @Test
    @DisplayName("LOOP_DETECTED_IN_PATH")
    public void loopDetectedInPath() {
        // Arrange
        when(neo4jDriver.session()).thenReturn(session);
        RouteRequest request = new RouteRequest();
        request.setStartLocation(new Coordinates(53.34, -6.26));
        request.setEndLocation(new Coordinates(53.35, -6.25));

        Stop stop1 = new Stop(); stop1.setStopId("1"); stop1.setStopName("Stop1"); stop1.setStopLat(53.341); stop1.setStopLon(-6.261);
        List<Stop> stops = List.of(stop1);
        when(stopRepository.findAll()).thenReturn(stops);

        Result result = mock(Result.class);
        when(result.hasNext()).thenReturn(true);
        Record record = mock(Record.class);
        when(result.single()).thenReturn(record);

        Value stopPathValue = mock(Value.class);
        when(record.get("stopPath")).thenReturn(stopPathValue);
        List<Map<String, Object>> stopPathList = List.of(
                Map.of("stopId", "1", "stopName", "Stop1", "stopLat", 53.341, "stopLon", -6.261),
                Map.of("stopId", "1", "stopName", "Stop1", "stopLat", 53.341, "stopLon", -6.261) // Loop
        );
        doReturn(stopPathList).when(stopPathValue).asList();

        Value rel = mock(Value.class);
        Relationship relationship = mock(Relationship.class);
        when(rel.asRelationship()).thenReturn(relationship);
        when(relationship.type()).thenReturn("RED_LUAS");
        when(rel.asMap()).thenReturn(Map.of("tripIds", List.of("T1"), "weight", 5.0));
        Value rawRelationshipsValue = mock(Value.class);
        when(record.get("rawRelationships")).thenReturn(rawRelationshipsValue);
        doReturn(List.of(rel)).when(rawRelationshipsValue).asList();
        when(record.get("totalWeight")).thenReturn(Values.value(5.0));
        when(session.run(anyString(), anyMap())).thenReturn(result);

        when(stopRepository.findById("1")).thenReturn(Optional.of(stop1));
        when(alphanumericGenerator.generateAlphanumericString()).thenReturn("ROUTE789");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> transitService.findRoutes(request));
        //assertTrue(exception.getMessage().contains("No routes found"));
    }

    @Test
    @DisplayName("EMPTY_STOP_LIST")
    public void emptyStopList() {
        // Arrange
        when(neo4jDriver.session()).thenReturn(session);
        RouteRequest request = new RouteRequest();
        request.setStartLocation(new Coordinates(53.34, -6.26));
        request.setEndLocation(new Coordinates(53.35, -6.25));
        when(stopRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> transitService.findRoutes(request));
        assertTrue(exception.getMessage().contains("No routes found"));
    }

    @Test
    @DisplayName("TRIP_NOT_FOUND")
    public void tripNotFound() {
        // Arrange
        when(neo4jDriver.session()).thenReturn(session);
        RouteRequest request = new RouteRequest();
        request.setStartLocation(new Coordinates(53.34, -6.26));
        request.setEndLocation(new Coordinates(53.35, -6.25));

        Stop stop1 = new Stop(); stop1.setStopId("1"); stop1.setStopName("Stop1"); stop1.setStopLat(53.341); stop1.setStopLon(-6.261);
        Stop stop2 = new Stop(); stop2.setStopId("2"); stop2.setStopName("Stop2"); stop2.setStopLat(53.351); stop2.setStopLon(-6.251);
        List<Stop> stops = Arrays.asList(stop1, stop2);
        when(stopRepository.findAll()).thenReturn(stops);

        Result result = mock(Result.class);
        when(result.hasNext()).thenReturn(true);
        Record record = mock(Record.class);
        when(result.single()).thenReturn(record);

        Value stopPathValue = mock(Value.class);
        when(record.get("stopPath")).thenReturn(stopPathValue);
        List<Map<String, Object>> stopPathList = List.of(
                Map.of("stopId", "1", "stopName", "Stop1", "stopLat", 53.341, "stopLon", -6.261),
                Map.of("stopId", "2", "stopName", "Stop2", "stopLat", 53.351, "stopLon", -6.251)
        );
        doReturn(stopPathList).when(stopPathValue).asList();

        Value rel = mock(Value.class);
        Relationship relationship = mock(Relationship.class);
        when(rel.asRelationship()).thenReturn(relationship);
        when(relationship.type()).thenReturn("RED_LUAS");
        when(rel.asMap()).thenReturn(Map.of("tripIds", List.of("T1"), "weight", 5.0));
        Value rawRelationshipsValue = mock(Value.class);
        when(record.get("rawRelationships")).thenReturn(rawRelationshipsValue);
        doReturn(List.of(rel)).when(rawRelationshipsValue).asList();
        when(record.get("totalWeight")).thenReturn(Values.value(10.0));
        when(session.run(anyString(), anyMap())).thenReturn(result);

        when(stopRepository.findById("1")).thenReturn(Optional.of(stop1));
        when(stopRepository.findById("2")).thenReturn(Optional.of(stop2));
        when(tripRepository.findById("T1")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> transitService.findRoutes(request));
        //assertTrue(exception.getMessage().contains("Trip not found"));
    }

    @Test
    @DisplayName("CALCULATE_DISTANCE_EMPTY_PATH")
    public void calculateDistanceEmptyPath() {
        // Act
        double distance1 = ReflectionTestUtils.invokeMethod(transitService, "calculateTravelDistance", (List<Coordinates>) null);
        double distance2 = ReflectionTestUtils.invokeMethod(transitService, "calculateTravelDistance", Collections.emptyList());

        // Assert
        assertEquals(0.0, distance1);
        assertEquals(0.0, distance2);
    }

    @Test
    @DisplayName("FILTER_TRANSIT_PATH_EMPTY")
    public void filterTransitPathEmpty() {
        // Act
        List<Coordinates> result1 = ReflectionTestUtils.invokeMethod(transitService, "filterTransitPathByStops", Collections.emptyList(), Collections.emptyList());
        List<Coordinates> result2 = ReflectionTestUtils.invokeMethod(transitService, "filterTransitPathByStops", List.of(new Stop()), Collections.emptyList());

        // Assert
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }

    @Test
    @DisplayName("FIND_NEAREST_COORDINATE_NO_MATCH")
    public void findNearestCoordinateNoMatch() {
        // Arrange
        Stop stop = new Stop(); stop.setStopLat(53.34); stop.setStopLon(-6.26);
        List<Coordinates> coords = List.of(new Coordinates(54.0, -7.0)); // Far away

        // Act
        Coordinates result = ReflectionTestUtils.invokeMethod(transitService, "findNearestCoordinate", stop, coords);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("HAVERSINE_DISTANCE_CALCULATION")
    public void haversineDistanceCalculation() {
        // Act
        double distance = ReflectionTestUtils.invokeMethod(transitService, "haversineDistance", 53.34, -6.26, 53.35, -6.25);

        // Assert
        assertTrue(distance > 1.0 && distance < 2.0); // Approx 1.4 km between these points
    }
}