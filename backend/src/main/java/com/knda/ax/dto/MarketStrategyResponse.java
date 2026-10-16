package com.knda.ax.dto;

import java.util.List;

public record MarketStrategyResponse(
        String regionLabel,
        String customerLabel,
        String marketOpportunity,
        List<String> coreCustomerValues,
        String recommendedMessage,
        List<String> recommendedChannels,
        String newsSearchQuery,
        List<NaverNewsArticle> newsArticles,
        String newsStatusMessage
) {
}
