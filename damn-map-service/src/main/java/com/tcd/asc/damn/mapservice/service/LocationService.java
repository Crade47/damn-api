package com.tcd.asc.damn.mapservice.service;

import com.tcd.asc.damn.common.entity.Location;
import com.tcd.asc.damn.common.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public List<Location> findLocationsByName(String name) {
        return locationRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Location> getAllLocations() {
        return this.locationRepository.findAll();
    }

    public Location addLocation(Location location) {
        return this.locationRepository.save(location);
    }
}