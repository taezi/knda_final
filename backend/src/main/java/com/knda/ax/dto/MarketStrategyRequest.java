package com.knda.ax.dto;

import jakarta.validation.constraints.NotBlank;

public record MarketStrategyRequest(
        @NotBlank String region,
        @NotBlank String customerSegment
) {
}
