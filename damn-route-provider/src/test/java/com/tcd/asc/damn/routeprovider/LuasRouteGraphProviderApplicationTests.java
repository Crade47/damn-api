package com.tcd.asc.damn.routeprovider;

import com.tcd.asc.damn.common.entity.LuasRoute;
import com.tcd.asc.damn.common.restclient.DataProviderClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@SpringBootTest
public class LuasRouteGraphProviderApplicationTests {

    @MockBean
    private DataProviderClient dataProviderClient; // Mock the Feign client



    @BeforeEach
    void setUp() {
        List<LuasRoute> mockRoutes = List.of();
        Mockito.when(dataProviderClient.getAllLuasRoutes()).thenReturn(mockRoutes);
    }

    @Test
    void contextLoads() {
        // Test should pass without real HTTP calls
    }
}
