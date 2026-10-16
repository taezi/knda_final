package com.knda.ax.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.news.naver")
public record NaverNewsProperties(
        String clientId,
        String clientSecret
) {
    public boolean isConfigured() {
        return isRealValue(clientId) && isRealValue(clientSecret);
    }

    private boolean isRealValue(String value) {
        return value != null && !value.isBlank() && !value.startsWith("your-naver-");
    }
}
