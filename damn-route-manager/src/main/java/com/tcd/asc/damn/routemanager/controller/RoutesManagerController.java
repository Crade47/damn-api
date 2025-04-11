package com.tcd.asc.damn.routemanager.controller;


import com.tcd.asc.damn.common.model.request.RouteRequest;
import com.tcd.asc.damn.common.model.response.RouteResponse;
import com.tcd.asc.damn.common.model.response.RoutesResponse;
import com.tcd.asc.damn.routemanager.service.RoutesManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes-manager")
public class RoutesManagerController {

    @Autowired
    private RoutesManagerService routesManagerService;

    @PostMapping("/get-routes")
    public RoutesResponse findRoutes(@RequestBody RouteRequest routeRequest) {
        return routesManagerService.getRoutes(routeRequest);
    }
}
