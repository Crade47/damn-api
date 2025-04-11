package com.tcd.asc.damn.routemanager.service;


import com.tcd.asc.damn.common.model.request.RouteRequest;
import com.tcd.asc.damn.common.model.response.RoutesResponse;
import com.tcd.asc.damn.common.restclient.RouteProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoutesManagerService {

    @Autowired
    private RouteProviderClient routeProviderClient;

    public RoutesResponse getRoutes(RouteRequest routeRequest) {
        return routeProviderClient.getRoute(routeRequest);
    }
}
