package com.knda.ax.dto;

import java.util.List;

public record FinalAxRecommendationResponse(
        String verdict,
        String recommendation,
        String nextAction,
        boolean generatedByCodex,
        String statusMessage,
        List<String> decisionReasons
) {
}
