package com.knda.ax.dto;

import java.math.BigDecimal;

public record PilotValidationResponse(
        BigDecimal pilotChangePercent,
        BigDecimal controlChangePercent,
        BigDecimal estimatedEffectPercentagePoint,
        String recommendation,
        String reason
) {
}
