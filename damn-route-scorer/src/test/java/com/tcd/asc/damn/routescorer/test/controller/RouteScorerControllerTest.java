package com.tcd.asc.damn.routescorer.test.controller;

import com.tcd.asc.damn.common.model.response.RouteResponse;
import com.tcd.asc.damn.routescorer.test.service.RouteScorerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RouteScorerController.class)
class RouteScorerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteScorerService routeScorerService;

    @Test
    void testGetRouteScore() throws Exception {
        RouteResponse routeResponse = new RouteResponse();
        Mockito.when(routeScorerService.getScore(Mockito.any(RouteResponse.class))).thenReturn(2.0);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/routes-scorer/get-score")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(routeResponse)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("2.0"));
    }
}