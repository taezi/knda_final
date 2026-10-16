package com.knda.ax.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DeliverySimulationRequest(
        @NotNull @Min(1) Integer doorWidth,
        @NotNull @Min(1) Integer doorHeight,
        @NotNull @Min(1) Integer elevatorDoorWidth,
        @NotNull @Min(1) Integer elevatorDoorHeight,
        @NotNull @Min(1) Integer elevatorCabinWidth,
        @NotNull @Min(1) Integer elevatorCabinDepth,
        @NotNull @Min(1) Integer wallWidth,
        @NotNull @Min(1) Integer wallHeight
) {
}
