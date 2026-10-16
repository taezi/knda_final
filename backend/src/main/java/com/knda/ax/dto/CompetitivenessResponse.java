package com.knda.ax.dto;

import java.util.List;

public record CompetitivenessResponse(
        List<ProductCompetitivenessResult> products,
        String recommendedProduct,
        String salesMessage,
        String reason
) {
}
