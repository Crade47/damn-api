package com.tcd.asc.damn.routemanager;

import com.tcd.asc.damn.common.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(JpaConfig.class)
public class RoutesManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoutesManagerApplication.class, args);
    }

}
