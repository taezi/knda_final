package com.knda.ax.controller;

import com.knda.ax.dto.ManufacturerFeasibilityRequest;
import com.knda.ax.dto.ManufacturerFeasibilityResponse;
import com.knda.ax.service.ManufacturerFeasibilityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manufacturer-feasibility")
public class ManufacturerFeasibilityController {

    private final ManufacturerFeasibilityService manufacturerFeasibilityService;

    public ManufacturerFeasibilityController(ManufacturerFeasibilityService manufacturerFeasibilityService) {
        this.manufacturerFeasibilityService = manufacturerFeasibilityService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ManufacturerFeasibilityResponse> analyze(
            @Valid @RequestBody ManufacturerFeasibilityRequest request
    ) {
        return ResponseEntity.ok(manufacturerFeasibilityService.analyze(request));
    }
}
