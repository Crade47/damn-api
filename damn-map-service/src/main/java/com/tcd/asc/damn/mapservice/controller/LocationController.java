package com.tcd.asc.damn.mapservice.controller;

import com.tcd.asc.damn.common.entity.Location;
import com.tcd.asc.damn.mapservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map-service")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/location-search")
    public List<Location> getLocationsByName(@RequestParam String name) {
        return locationService.findLocationsByName(name);
    }

    // POST endpoint to add a new location
    @PostMapping
    public Location addLocation(@RequestBody Location location) {
        locationService.addLocation(location);
        return location;  // Return the added location as confirmation
    }
}