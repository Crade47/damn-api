package com.tcd.asc.damn.routescorer.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=test")
class LuasRouteScorerApplicationTests {

    @Test
    void contextLoads() {
    }

}
