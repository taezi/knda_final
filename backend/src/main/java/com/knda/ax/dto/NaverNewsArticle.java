package com.knda.ax.dto;

public record NaverNewsArticle(
        String title,
        String source,
        String publishedAt,
        String summary,
        String originalLink
) {
}
