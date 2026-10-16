package com.knda.ax.controller;

import com.knda.ax.dto.B2bRoiRequest;
import com.knda.ax.dto.B2bRoiResponse;
import com.knda.ax.service.B2bRoiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/b2b-roi")
public class B2bRoiController {

    private final B2bRoiService b2bRoiService;

    public B2bRoiController(B2bRoiService b2bRoiService) {
        this.b2bRoiService = b2bRoiService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<B2bRoiResponse> calculate(@Valid @RequestBody B2bRoiRequest request) {
        return ResponseEntity.ok(b2bRoiService.calculate(request));
    }
}
