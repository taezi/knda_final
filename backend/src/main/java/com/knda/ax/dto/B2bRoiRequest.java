package com.knda.ax.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record B2bRoiRequest(
        @NotNull @DecimalMin(value = "1.0") BigDecimal totalInvestment,
        @NotNull @DecimalMin(value = "0.0") BigDecimal monthlyAdditionalRevenue,
        @NotNull @DecimalMin(value = "0.0") @DecimalMax(value = "100.0") BigDecimal contributionMarginPercent,
        @NotNull @DecimalMin(value = "0.0") BigDecimal monthlyOperatingCost
) {
}
