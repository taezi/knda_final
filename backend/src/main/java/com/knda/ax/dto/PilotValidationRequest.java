package com.knda.ax.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PilotValidationRequest(
        @NotNull @Min(1) Integer pilotStoreCount,
        @NotNull @Min(1) Integer controlStoreCount,
        @NotNull @DecimalMin(value = "1.0") BigDecimal pilotBeforeSales,
        @NotNull @DecimalMin(value = "0.0") BigDecimal pilotAfterSales,
        @NotNull @DecimalMin(value = "1.0") BigDecimal controlBeforeSales,
        @NotNull @DecimalMin(value = "0.0") BigDecimal controlAfterSales
) {
}
