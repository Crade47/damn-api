package com.tcd.asc.damn.common.restclient;

import com.tcd.asc.damn.common.model.request.RouteRequest;
import com.tcd.asc.damn.common.model.response.RoutesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "route-provider", url = "http://${damn.host}:8083", path = "/api/routes-provider")
public interface RouteProviderClient {

    @PostMapping("/get-routes")
    RoutesResponse getRoute(@RequestBody RouteRequest routeRequest);

}