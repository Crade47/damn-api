package com.tcd.asc.damn.routescorer.test.controller;


import com.tcd.asc.damn.common.model.response.RouteResponse;
import com.tcd.asc.damn.routescorer.test.service.RouteScorerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes-scorer")
public class RouteScorerController {

    @Autowired
    private RouteScorerService routeScorerService;

    @PostMapping("/get-score")
    public Double getRouteScore(@RequestBody RouteResponse routeResponse) {
        return routeScorerService.getScore(routeResponse);
    }
}
