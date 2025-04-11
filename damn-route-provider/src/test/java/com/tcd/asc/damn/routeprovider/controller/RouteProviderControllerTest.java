package com.tcd.asc.damn.routeprovider.controller;
import com.tcd.asc.damn.common.model.response.RouteResponse; 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcd.asc.damn.common.model.dto.Coordinates;
import com.tcd.asc.damn.common.model.request.RouteRequest;
import com.tcd.asc.damn.common.model.response.RoutesResponse;
import com.tcd.asc.damn.routeprovider.service.TransitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RouteProviderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransitService transitService;

    @InjectMocks
    private RouteProviderController routeProviderController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(routeProviderController).build();
    }

    @Test
    public void testGetRoute_Success() throws Exception { // Given
        RouteRequest routeRequest = createSampleRouteRequest();
        RoutesResponse routesResponse = createSampleRoutesResponse();
        
        when(transitService.findRoutes(any(RouteRequest.class))).thenReturn(routesResponse);

        // When & Then
        mockMvc.perform(post("/api/routes-provider/get-routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(routeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeId").value("ROUTE123"));
    }

    // Helper methods to create test data
    private RouteRequest createSampleRouteRequest() {
        // You'll need to create RouteRequest with appropriate fields
        // This depends on your actual RouteRequest class structure
        RouteRequest request = new RouteRequest();
        // Set location data based on your model
        return request;
    }

    private RoutesResponse createSampleRoutesResponse() {
        RoutesResponse response = new RoutesResponse();
        
        // Set the same coordinates as in the request for consistency
        Coordinates startLocation = new Coordinates(53.3498, -6.2603);
        Coordinates endLocation = new Coordinates(53.4233, -6.1350);
        
        response.setStartLocation(startLocation);
        response.setEndLocation(endLocation);
        
        // Create a sample route response
        
        List<RouteResponse> routeResponses = new ArrayList<RouteResponse>();
        RouteResponse routeResponse = new RouteResponse();
        routeResponse.setRouteId("ROUTE123"); // Assuming RouteResponse has a routeId field
        
        // Add any other required fields to the RouteResponse object
        // This depends on the actual structure of your RouteResponse class
        
        routeResponses.add(routeResponse);
        
        response.setRouteResponses(routeResponses);
        response.setNoOfRoutes(routeResponses.size());
        
        return response;
    }
}
