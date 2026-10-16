package com.knda.ax.dto;

import java.math.BigDecimal;

public record B2bRoiResponse(
        BigDecimal monthlyContributionProfit,
        BigDecimal paybackPeriodMonths,
        String recommendation,
        String reason
) {
}
