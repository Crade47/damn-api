package com.tcd.asc.damn.dataprovider.controller;


import com.tcd.asc.damn.common.constants.StationType;
import com.tcd.asc.damn.common.entity.Location;
import com.tcd.asc.damn.common.entity.LuasRoute;
import com.tcd.asc.damn.common.entity.Station;
import com.tcd.asc.damn.dataprovider.service.DataProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data-provider")
public class DataProviderController {

    @Autowired
    private DataProviderService dataProviderService;

    @GetMapping("/stations")
    public List<Station> getStations(@RequestParam String stationType) {
        return dataProviderService.getAllStations(StationType.valueOf(stationType));
    }

    @PostMapping("/near-luas-station")
    public Station getNearestStation(@RequestBody Location location ) {
        return dataProviderService.findNearestStation(location);
    }

    @GetMapping("/in-between-station")
    public List<String> getBetweenStations(@RequestParam Long startStationId, @RequestParam Long endStationId) {
        return dataProviderService.getStationNamesBetween(startStationId, endStationId);
    }

    @GetMapping("/all-luas-route")
    public List<LuasRoute> getAllLuasRoutes() {
        return dataProviderService.getAllLuasRoutes();
    }
}
