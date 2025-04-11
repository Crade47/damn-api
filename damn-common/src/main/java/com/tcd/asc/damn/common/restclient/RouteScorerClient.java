package com.tcd.asc.damn.common.restclient;

import com.tcd.asc.damn.common.model.response.RouteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "route-scorer", url = "http://${damn.host}:8085", path = "/api/routes-scorer")
public interface RouteScorerClient {

    @PostMapping("/get-score")
    Double getRouteScore(@RequestBody RouteResponse routeResponse);

}