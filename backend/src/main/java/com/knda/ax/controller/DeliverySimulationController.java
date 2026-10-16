package com.knda.ax.controller;

import com.knda.ax.dto.DeliverySimulationRequest;
import com.knda.ax.dto.DeliverySimulationResponse;
import com.knda.ax.service.DeliverySimulationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery-simulation")
public class DeliverySimulationController {

    private final DeliverySimulationService deliverySimulationService;

    public DeliverySimulationController(DeliverySimulationService deliverySimulationService) {
        this.deliverySimulationService = deliverySimulationService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<DeliverySimulationResponse> analyze(@Valid @RequestBody DeliverySimulationRequest request) {
        return ResponseEntity.ok(deliverySimulationService.simulate(request));
    }
}
