package com.knda.ax.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FinalAxRecommendationRequest(
        @NotBlank String regionLabel,
        @NotBlank String customerLabel,
        @NotBlank String priorityLabel,
        @NotBlank String recommendedProduct,
        @NotBlank String marketOpportunity,
        BigDecimal roiPaybackPeriodMonths,
        @NotNull BigDecimal monthlyContributionProfit,
        @NotBlank String roiRecommendation,
        @NotNull Double pilotEffectPercentagePoint,
        boolean pilotDataVerified,
        boolean marketFitVerified,
        @NotBlank String pilotRecommendation,
        @NotNull Double perUnitSavings,
        Double capexPaybackPeriodYears,
        boolean wallInstallPossible,
        boolean deliveryRoutePossible,
        boolean financialDataVerified,
        boolean deliverySiteVerified,
        @NotBlank String recommendedDeliveryMethod
) {
}
