package com.tcd.asc.damn.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.tcd.asc.damn.common.entity")
@EnableJpaRepositories(basePackages = "com.tcd.asc.damn.common.repository")
@ComponentScan(basePackages = "com.tcd.asc.damn.common")
@EnableFeignClients(basePackages = "com.tcd.asc.damn.common.restclient")
public class JpaConfig {
}
