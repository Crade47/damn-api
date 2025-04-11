package com.tcd.asc.damn.damnapigateway.component;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            System.out.println("Request Path: " + exchange.getRequest().getPath());
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuration properties (if any)
    }
}
