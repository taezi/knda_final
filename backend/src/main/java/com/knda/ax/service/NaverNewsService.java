package com.knda.ax.service;

import com.knda.ax.config.NaverNewsProperties;
import com.knda.ax.dto.NaverNewsArticle;
import com.knda.ax.dto.NaverNewsSearchResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class NaverNewsService {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private final NaverNewsProperties properties;
    private final RestClient restClient;

    public NaverNewsService(NaverNewsProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.create();
    }

    public NaverNewsSearchResult searchRelevant(String query, List<String> productKeywords, List<String> regionKeywords, List<String> customerKeywords) {
        if (!properties.isConfigured()) {
            return new NaverNewsSearchResult(List.of(), "NAVER API HUB 키를 설정하면 최신 시장 기사를 표시합니다.");
        }

        try {
            String responseBody = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("naverapihub.apigw.ntruss.com")
                            .path("/search/v1/news")
                            .queryParam("query", query)
                            .queryParam("display", 15)
                            .queryParam("start", 1)
                            .queryParam("sort", "date")
                            .queryParam("format", "json")
                            .build())
                    .header("X-NCP-APIGW-API-KEY-ID", properties.clientId())
                    .header("X-NCP-APIGW-API-KEY", properties.clientSecret())
                    .retrieve()
                    .body(String.class);
            JsonNode response = JSON_MAPPER.readTree(responseBody);

            List<ScoredArticle> candidates = new ArrayList<>();
            for (JsonNode item : response.path("items")) {
                String originalLink = item.path("originallink").asText();
                if (!StringUtils.hasText(originalLink)) {
                    originalLink = item.path("link").asText();
                }
                NaverNewsArticle article = new NaverNewsArticle(
                        cleanHtml(item.path("title").asText()),
                        sourceFrom(originalLink),
                        formatPublishedAt(item.path("pubDate").asText()),
                        cleanHtml(item.path("description").asText()),
                        originalLink
                );
                int relevanceScore = relevanceScore(article, productKeywords, regionKeywords, customerKeywords);
                if (isRelevant(article, productKeywords, regionKeywords, customerKeywords, relevanceScore)) {
                    candidates.add(new ScoredArticle(article, relevanceScore, publishedInstant(item.path("pubDate").asText())));
                }
            }
            List<NaverNewsArticle> articles = candidates.stream()
                    .sorted(Comparator.comparing(ScoredArticle::publishedAt).reversed())
                    .limit(3)
                    .map(ScoredArticle::article)
                    .toList();
            return new NaverNewsSearchResult(articles, articles.isEmpty()
                    ? "검색 결과 중 선택한 지역·고객군과 직접 관련된 기사를 찾지 못했습니다."
                    : "선택한 지역·고객군 조건을 통과한 기사 " + articles.size() + "건입니다.");
        } catch (RestClientResponseException exception) {
            return new NaverNewsSearchResult(List.of(), "시장 기사 조회가 거부되었습니다 (HTTP "
                    + exception.getStatusCode().value() + "). NAVER API HUB Client ID·Secret과 뉴스 검색 API 사용 설정을 확인해 주세요.");
        } catch (ResourceAccessException exception) {
            return new NaverNewsSearchResult(List.of(), "NAVER API HUB 서버에 연결할 수 없습니다. 네트워크 또는 방화벽 설정을 확인해 주세요.");
        } catch (RuntimeException exception) {
            return new NaverNewsSearchResult(List.of(), "시장 기사 응답 처리에 실패했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

    private String cleanHtml(String text) {
        return HtmlUtils.htmlUnescape(text.replaceAll("<[^>]*>", "")).trim();
    }

    private boolean isRelevant(NaverNewsArticle article, List<String> productKeywords, List<String> regionKeywords,
                               List<String> customerKeywords, int relevanceScore) {
        String title = article.title().toLowerCase();
        String fullText = (article.title() + " " + article.summary()).toLowerCase();
        boolean titleMentionsProductOrCustomer = containsAny(title, productKeywords) || containsAny(title, customerKeywords);
        boolean textMentionsRegionOrCustomer = containsAny(fullText, regionKeywords) || containsAny(fullText, customerKeywords);
        return titleMentionsProductOrCustomer && textMentionsRegionOrCustomer && relevanceScore >= 8;
    }

    private int relevanceScore(NaverNewsArticle article, List<String> productKeywords, List<String> regionKeywords,
                               List<String> customerKeywords) {
        String title = article.title().toLowerCase();
        String summary = article.summary().toLowerCase();
        return countMatches(title, productKeywords) * 5
                + countMatches(title, customerKeywords) * 5
                + countMatches(title, regionKeywords) * 3
                + countMatches(summary, productKeywords) * 2
                + countMatches(summary, customerKeywords) * 3
                + countMatches(summary, regionKeywords);
    }

    private int countMatches(String text, List<String> keywords) {
        return (int) keywords.stream().filter(keyword -> text.contains(keyword.toLowerCase())).count();
    }

    private boolean containsAny(String text, List<String> keywords) {
        return keywords.stream().anyMatch(keyword -> text.contains(keyword.toLowerCase()));
    }

    private String sourceFrom(String link) {
        try {
            return URI.create(link).getHost().replaceFirst("^www\\.", "");
        } catch (RuntimeException exception) {
            return "원문 기사";
        }
    }

    private String formatPublishedAt(String publishedAt) {
        try {
            return ZonedDateTime.parse(publishedAt, DateTimeFormatter.RFC_1123_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        } catch (RuntimeException exception) {
            return publishedAt;
        }
    }

    private Instant publishedInstant(String publishedAt) {
        try {
            return ZonedDateTime.parse(publishedAt, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        } catch (RuntimeException exception) {
            return Instant.EPOCH;
        }
    }

    private record ScoredArticle(NaverNewsArticle article, int score, Instant publishedAt) {
    }
}
