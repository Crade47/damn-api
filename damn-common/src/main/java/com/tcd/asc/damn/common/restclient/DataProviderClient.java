package com.tcd.asc.damn.common.restclient;

import com.tcd.asc.damn.common.entity.Location;
import com.tcd.asc.damn.common.entity.LuasRoute;
import com.tcd.asc.damn.common.entity.Station;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "data-manager", url = "http://${damn.host}:8084", path = "/api/data-manager")
public interface DataProviderClient {

    @GetMapping("/stations")
    List<Station> getStations(@RequestParam("stationType") String stationType);

    @PostMapping("/near-luas-station")
    Station findNearestStation(Location startLocation);

    @GetMapping("/in-between-station")
    List<String> getBetweenStations(@RequestParam Long startStationId, @RequestParam Long endStationId);

    @GetMapping("/all-luas-route")
    List<LuasRoute> getAllLuasRoutes();
}