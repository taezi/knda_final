package com.knda.ax.dto;

import java.util.List;

public record NaverNewsSearchResult(
        List<NaverNewsArticle> articles,
        String statusMessage
) {
}
