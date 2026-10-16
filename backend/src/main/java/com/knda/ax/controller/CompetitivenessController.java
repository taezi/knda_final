package com.knda.ax.controller;

import com.knda.ax.dto.CompetitivenessRequest;
import com.knda.ax.dto.CompetitivenessResponse;
import com.knda.ax.service.CompetitivenessService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/competitiveness")
public class CompetitivenessController {

    private final CompetitivenessService competitivenessService;

    public CompetitivenessController(CompetitivenessService competitivenessService) {
        this.competitivenessService = competitivenessService;
    }

    @PostMapping("/compare")
    public ResponseEntity<CompetitivenessResponse> compare(@Valid @RequestBody CompetitivenessRequest request) {
        return ResponseEntity.ok(competitivenessService.compare(request));
    }
}
