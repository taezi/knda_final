package com.knda.ax.dto;

import java.util.List;

public record DeliverySimulationResponse(
        Integer productWidth,
        Integer productHeight,
        boolean wallInstallPossible,
        List<DeliveryMethodResult> methods,
        String recommendedMethod,
        Integer recommendedModuleCount,
        String recommendation
) {
}
