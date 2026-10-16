package com.knda.ax.dto;

import java.math.BigDecimal;

public record ManufacturerFeasibilityResponse(
        BigDecimal ommBom,
        BigDecimal fippPanelCost,
        BigDecimal fippBom,
        BigDecimal perUnitSavings,
        BigDecimal annualSavings,
        BigDecimal manufacturingCostRatioPercent,
        BigDecimal paybackPeriodYears,
        String recommendation,
        String reason
) {
}
