package com.knda.ax.controller;

import com.knda.ax.dto.MarketStrategyRequest;
import com.knda.ax.dto.MarketStrategyResponse;
import com.knda.ax.service.MarketStrategyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market-strategy")
public class MarketStrategyController {

    private final MarketStrategyService marketStrategyService;

    public MarketStrategyController(MarketStrategyService marketStrategyService) {
        this.marketStrategyService = marketStrategyService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<MarketStrategyResponse> recommend(@Valid @RequestBody MarketStrategyRequest request) {
        return ResponseEntity.ok(marketStrategyService.recommend(request));
    }
}
