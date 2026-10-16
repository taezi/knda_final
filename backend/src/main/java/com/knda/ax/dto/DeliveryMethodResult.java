package com.knda.ax.dto;

public record DeliveryMethodResult(
        String method,
        Integer moduleCount,
        Integer packageWidth,
        Integer packageHeight,
        boolean doorPass,
        boolean elevatorDoorPass,
        boolean elevatorCabinPass,
        boolean deliveryPossible,
        String note
) {
}
