package com.knda.ax.controller;

import com.knda.ax.dto.PilotValidationRequest;
import com.knda.ax.dto.PilotValidationResponse;
import com.knda.ax.service.PilotValidationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pilot-validation")
public class PilotValidationController {

    private final PilotValidationService pilotValidationService;

    public PilotValidationController(PilotValidationService pilotValidationService) {
        this.pilotValidationService = pilotValidationService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<PilotValidationResponse> analyze(@Valid @RequestBody PilotValidationRequest request) {
        return ResponseEntity.ok(pilotValidationService.analyze(request));
    }
}
