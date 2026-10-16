package com.knda.ax.dto;

import jakarta.validation.constraints.NotBlank;

public record CompetitivenessRequest(
        @NotBlank String customerSegment,
        @NotBlank String priority
) {
}
