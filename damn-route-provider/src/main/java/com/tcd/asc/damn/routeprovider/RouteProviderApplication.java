package com.tcd.asc.damn.routeprovider;

import com.tcd.asc.damn.common.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(JpaConfig.class)
public class RouteProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouteProviderApplication.class, args);
    }

}
