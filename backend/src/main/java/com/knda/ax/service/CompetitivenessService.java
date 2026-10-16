package com.knda.ax.service;

import com.knda.ax.dto.CompetitivenessRequest;
import com.knda.ax.dto.CompetitivenessResponse;
import com.knda.ax.dto.ProductCompetitivenessResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompetitivenessService {

    private static final Map<String, Map<String, Integer>> PRODUCT_SCORES = Map.of(
            "FLiPP OLED", Map.of("picture", 5, "brightRoom", 5, "response", 5, "viewingAngle", 5, "price", 3, "installation", 4, "design", 5),
            "OMM OLED", Map.of("picture", 5, "brightRoom", 4, "response", 5, "viewingAngle", 5, "price", 2, "installation", 4, "design", 5),
            "Mini-LED", Map.of("picture", 4, "brightRoom", 4, "response", 4, "viewingAngle", 4, "price", 5, "installation", 4, "design", 3)
    );

    public CompetitivenessResponse compare(CompetitivenessRequest request) {
        Map<String, Integer> weights = createWeights(request.customerSegment(), request.priority());
        List<ProductCompetitivenessResult> products = new ArrayList<>();

        PRODUCT_SCORES.forEach((product, scores) -> products.add(new ProductCompetitivenessResult(
                product,
                calculateWeightedScore(scores, weights),
                strengthsFor(product)
        )));

        products.sort(Comparator.comparing(ProductCompetitivenessResult::score).reversed());
        ProductCompetitivenessResult recommendation = products.getFirst();

        return new CompetitivenessResponse(
                products,
                recommendation.product(),
                salesMessageFor(request.customerSegment(), recommendation.product()),
                reasonFor(request.customerSegment(), request.priority(), recommendation.product())
        );
    }

    private Map<String, Integer> createWeights(String customerSegment, String priority) {
        Map<String, Integer> weights = new LinkedHashMap<>();
        weights.put("picture", 20);
        weights.put("brightRoom", 12);
        weights.put("response", 10);
        weights.put("viewingAngle", 10);
        weights.put("price", 15);
        weights.put("installation", 13);
        weights.put("design", 20);

        switch (customerSegment) {
            case "HOME_THEATER" -> addWeights(weights, Map.of("picture", 15, "viewingAngle", 8, "brightRoom", 5));
            case "B2B_PREMIUM" -> addWeights(weights, Map.of("design", 12, "picture", 8, "installation", 6));
            case "SPORTS_BAR" -> addWeights(weights, Map.of("brightRoom", 12, "response", 8, "price", 8));
            case "CASINO" -> addWeights(weights, Map.of("picture", 12, "design", 10, "brightRoom", 6));
            default -> throw new IllegalArgumentException("지원하지 않는 고객군입니다.");
        }

        String priorityMetric = switch (priority) {
            case "QUALITY" -> "picture";
            case "PRICE" -> "price";
            case "INSTALLATION" -> "installation";
            case "DESIGN" -> "design";
            default -> throw new IllegalArgumentException("지원하지 않는 우선순위입니다.");
        };
        weights.compute(priorityMetric, (key, value) -> value + 20);
        return weights;
    }

    private void addWeights(Map<String, Integer> weights, Map<String, Integer> additions) {
        additions.forEach((key, value) -> weights.compute(key, (metric, current) -> current + value));
    }

    private BigDecimal calculateWeightedScore(Map<String, Integer> scores, Map<String, Integer> weights) {
        int totalWeight = weights.values().stream().mapToInt(Integer::intValue).sum();
        BigDecimal weightedSum = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> weight : weights.entrySet()) {
            weightedSum = weightedSum.add(BigDecimal.valueOf(scores.get(weight.getKey())).multiply(BigDecimal.valueOf(weight.getValue())));
        }

        return weightedSum.divide(BigDecimal.valueOf(totalWeight), 1, RoundingMode.HALF_UP);
    }

    private List<String> strengthsFor(String product) {
        return switch (product) {
            case "FLiPP OLED" -> List.of("밝은 공간에서도 유지되는 OLED 화질", "완전한 블랙과 넓은 시야각", "공정 효율 개선 가능성");
            case "OMM OLED" -> List.of("검증된 OLED 화질", "빠른 응답속도", "초슬림 디자인");
            default -> List.of("상대적 가격 경쟁력", "밝은 공간 사용성", "폭넓은 제품 선택지");
        };
    }

    private String salesMessageFor(String customerSegment, String product) {
        if (!"FLiPP OLED".equals(product)) {
            return product + "은 선택한 조건에서 비용 또는 운영 효율 측면의 대안입니다.";
        }

        return switch (customerSegment) {
            case "HOME_THEATER" -> "낮과 밤을 가리지 않는 100인치 프라이빗 시네마";
            case "B2B_PREMIUM" -> "공간의 격을 완성하는 시그니처 스크린";
            case "SPORTS_BAR" -> "경기를 보는 펍에서, 몰입하는 펍으로";
            case "CASINO" -> "VIP 존에 완성하는 압도적 화질 경험";
            default -> "100인치 FLiPP OLED의 프리미엄 가치";
        };
    }

    private String reasonFor(String customerSegment, String priority, String product) {
        return segmentLabel(customerSegment) + "에서 " + priorityLabel(priority) + " 항목을 우선 반영한 결과, " + product + "가 가장 높은 가중 평균 점수를 받았습니다.";
    }

    private String segmentLabel(String customerSegment) {
        return switch (customerSegment) {
            case "HOME_THEATER" -> "B2C 하이엔드 홈시네마 고객군";
            case "B2B_PREMIUM" -> "B2B 프리미엄 공간";
            case "SPORTS_BAR" -> "스포츠 펍·바 체인";
            case "CASINO" -> "카지노·통합 리조트";
            default -> "선택한 고객군";
        };
    }

    private String priorityLabel(String priority) {
        return switch (priority) {
            case "QUALITY" -> "화질";
            case "PRICE" -> "가격";
            case "INSTALLATION" -> "설치 편의";
            case "DESIGN" -> "디자인";
            default -> "선택한 조건";
        };
    }
}
