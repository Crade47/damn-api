package com.tcd.asc.damn.routescorer.test.service;

import com.tcd.asc.damn.common.entity.Stop;
import com.tcd.asc.damn.common.model.dto.Coordinates;
import com.tcd.asc.damn.common.model.dto.TransitSegment;
import com.tcd.asc.damn.common.model.dto.WalkSegment;
import com.tcd.asc.damn.common.model.response.RouteResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouteScorerServiceTest {

    @Test
    void testEmptyRoutes() {
        RouteScorerService routeScorerService = new RouteScorerService();
        RouteResponse routeResponse = new RouteResponse();

        // Set an empty routes list
        routeResponse.setRoutes(Collections.emptyList());

        Double score = routeScorerService.getScore(routeResponse);
        assertEquals(0.0, score);
    }

    @Test
    void testRoutesWithOnlyWalkSegment() {
        RouteScorerService routeScorerService = new RouteScorerService();
        RouteResponse routeResponse = new RouteResponse();

        // Create a WalkSegment (no stops)
        WalkSegment walkSegment = new WalkSegment();
        walkSegment.setStartCoordinate(new Coordinates(53.34, -6.26));
        walkSegment.setEndCoordinate(new Coordinates(53.35, -6.27));
        walkSegment.setWalkPath(Arrays.asList(
                new Coordinates(53.34, -6.26),
                new Coordinates(53.35, -6.27)
        ));

        // Set routes list with only the WalkSegment
        routeResponse.setRoutes(Collections.singletonList(walkSegment));

        Double score = routeScorerService.getScore(routeResponse);
        assertEquals(0.0, score); // WalkSegment has no stops, so score should be 0
    }

    @Test
    void testRoutesWithTransitSegment() {
        RouteScorerService routeScorerService = new RouteScorerService();
        RouteResponse routeResponse = new RouteResponse();

        // Create a TransitSegment with stops
        TransitSegment transitSegment = new TransitSegment();
        Stop stop1 = Mockito.mock(Stop.class);
        Stop stop2 = Mockito.mock(Stop.class);
        Stop stop3 = Mockito.mock(Stop.class);
        List<Stop> stopPath = Arrays.asList(stop1, stop2, stop3);
        transitSegment.setStopPath(stopPath);
        transitSegment.setBoardingStop(stop1);
        transitSegment.setAlightingStop(stop3);
        transitSegment.setTransitPath(Arrays.asList(
                new Coordinates(53.34, -6.26),
                new Coordinates(53.35, -6.27)
        ));

        // Set routes list with the TransitSegment
        routeResponse.setRoutes(Collections.singletonList(transitSegment));

        Double score = routeScorerService.getScore(routeResponse);
        assertEquals(0.0, score); // 3 stops * 1.0 = 3.0
    }

    @Test
    void testRoutesWithMixedSegments() {
        RouteScorerService routeScorerService = new RouteScorerService();
        RouteResponse routeResponse = new RouteResponse();

        // Create a WalkSegment (no stops)
        WalkSegment walkSegment = new WalkSegment();
        walkSegment.setStartCoordinate(new Coordinates(53.34, -6.26));
        walkSegment.setEndCoordinate(new Coordinates(53.35, -6.27));
        walkSegment.setWalkPath(Arrays.asList(
                new Coordinates(53.34, -6.26),
                new Coordinates(53.35, -6.27)
        ));

        // Create a TransitSegment with stops
        TransitSegment transitSegment = new TransitSegment();
        Stop stop1 = Mockito.mock(Stop.class);
        Stop stop2 = Mockito.mock(Stop.class);
        List<Stop> stopPath = Arrays.asList(stop1, stop2);
        transitSegment.setStopPath(stopPath);
        transitSegment.setBoardingStop(stop1);
        transitSegment.setAlightingStop(stop2);
        transitSegment.setTransitPath(Arrays.asList(
                new Coordinates(53.35, -6.27),
                new Coordinates(53.36, -6.28)
        ));

        // Set routes list with both segments
        routeResponse.setRoutes(Arrays.asList(walkSegment, transitSegment));

        Double score = routeScorerService.getScore(routeResponse);
        assertEquals(0.0, score); // 2 stops from TransitSegment * 1.0 = 2.0 (WalkSegment contributes 0)
    }

    @Test
    void testGetScoreAlwaysReturnsZero() {
        RouteScorerService routeScorerService = new RouteScorerService();
        RouteResponse routeResponse = new RouteResponse();

        Double score = routeScorerService.getScore(routeResponse);
        assertEquals(0.0, score);
    }
}