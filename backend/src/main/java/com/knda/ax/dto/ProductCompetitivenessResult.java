package com.knda.ax.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductCompetitivenessResult(
        String product,
        BigDecimal score,
        List<String> strengths
) {
}
