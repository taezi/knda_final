package com.knda.ax.controller;

import com.knda.ax.dto.FinalAxRecommendationRequest;
import com.knda.ax.dto.FinalAxRecommendationResponse;
import com.knda.ax.service.FinalAxRecommendationService;
import com.knda.ax.service.ScenarioAxRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/final-ax-recommendation")
public class FinalAxRecommendationController {

    private final FinalAxRecommendationService finalAxRecommendationService;
    private final ScenarioAxRecommendationService scenarioAxRecommendationService;

    public FinalAxRecommendationController(
            FinalAxRecommendationService finalAxRecommendationService,
            ScenarioAxRecommendationService scenarioAxRecommendationService
    ) {
        this.finalAxRecommendationService = finalAxRecommendationService;
        this.scenarioAxRecommendationService = scenarioAxRecommendationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<FinalAxRecommendationResponse> generate(
            @Valid @RequestBody FinalAxRecommendationRequest request
    ) {
        return ResponseEntity.ok(finalAxRecommendationService.recommend(request));
    }

    @PostMapping("/scenario")
    public ResponseEntity<FinalAxRecommendationResponse> generateScenario(
            @Valid @RequestBody FinalAxRecommendationRequest request
    ) {
        return ResponseEntity.ok(scenarioAxRecommendationService.recommend(request));
    }
}
