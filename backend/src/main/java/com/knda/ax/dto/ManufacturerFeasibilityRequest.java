package com.knda.ax.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ManufacturerFeasibilityRequest(
        @NotNull @DecimalMin(value = "1.0") BigDecimal ommPanelCost,
        @NotNull @DecimalMin(value = "1.0") @DecimalMax(value = "100.0") BigDecimal ommYieldPercent,
        @NotNull @DecimalMin(value = "1.0") @DecimalMax(value = "100.0") BigDecimal fippYieldPercent,
        @NotNull @DecimalMin(value = "0.0") @DecimalMax(value = "80.0") BigDecimal fippDirectCostReductionPercent,
        @NotNull @DecimalMin(value = "0.0") BigDecimal componentAndAssemblyCost,
        @NotNull @DecimalMin(value = "0.0") BigDecimal logisticsAndPackagingCost,
        @NotNull @Min(1) Integer annualProductionVolume,
        @NotNull @DecimalMin(value = "1.0") BigDecimal capex,
        @NotNull @DecimalMin(value = "1.0") BigDecimal sellingPrice
) {
}
