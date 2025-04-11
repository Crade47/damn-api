package com.tcd.asc.damn.dataprovider.controller;

import com.tcd.asc.damn.dataprovider.service.GraphInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data-manager")
public class DataManagerController {

    @Autowired
    private GraphInitializer graphInitializer;

    @PostMapping("/initialize")
    public ResponseEntity<String> initializeGraph() {
        try {
            graphInitializer.initializeGraph();
            return ResponseEntity.ok("Graph initialized successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to initialize graph: " + e.getMessage());
        }
    }
}