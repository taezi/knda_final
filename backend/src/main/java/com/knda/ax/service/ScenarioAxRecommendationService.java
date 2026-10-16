package com.knda.ax.service;

import com.knda.ax.dto.FinalAxRecommendationRequest;
import com.knda.ax.dto.FinalAxRecommendationResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;

/** Builds a transparent, template-based recommendation without invoking Codex or another AI API. */
@Service
public class ScenarioAxRecommendationService {

    private final FinalAxDecisionPolicy decisionPolicy;

    public ScenarioAxRecommendationService(FinalAxDecisionPolicy decisionPolicy) {
        this.decisionPolicy = decisionPolicy;
    }

    public FinalAxRecommendationResponse recommend(FinalAxRecommendationRequest request) {
        FinalAxDecisionPolicy.Decision decision = decisionPolicy.evaluate(request);
        String recommendation = buildRecommendation(request, decision.verdict());

        return new FinalAxRecommendationResponse(
                decision.verdict(),
                recommendation,
                buildNextAction(request, decision),
                false,
                "Codex 없이 6개 도구 입력값과 프로젝트 판정 기준으로 작성한 시나리오 권고입니다.",
                decision.reasons()
        );
    }

    private String buildRecommendation(FinalAxRecommendationRequest request, String verdict) {
        String productFit = request.recommendedProduct().toLowerCase().contains("flipp")
                ? request.recommendedProduct() + "가 " + request.priorityLabel() + "을 중시하는 " + request.customerLabel() + "의 선택 조건에 부합합니다."
                : "선택 조건에서 경쟁력 1위는 " + request.recommendedProduct() + "이며, FLiPP OLED의 고객 적합성을 다시 살펴볼 필요가 있습니다.";

        String businessSummary = "B2B ROI 회수기간은 " + formatMonths(request.roiPaybackPeriodMonths())
                + ", 파일럿 효과는 " + format(request.pilotEffectPercentagePoint()) + "%p(" + (request.pilotDataVerified() ? "실측" : "가정")
                + "), 대당 원가 절감은 " + formatCurrency(request.perUnitSavings())
                + ", 제조 CAPEX 회수기간은 " + formatYears(request.capexPaybackPeriodYears()) + "입니다.";
        String installation = request.wallInstallPossible() && request.deliveryRoutePossible()
                ? request.recommendedDeliveryMethod() + " 방식으로 규격상 반입과 설치가 가능하지만, 현장 실측 여부도 확인해야 합니다."
                : "현재 입력한 조건에서는 설치 공간 또는 반입 경로에 보완이 필요합니다.";

        if ("보완 필요".equals(verdict)) {
            return request.regionLabel() + "의 " + request.customerLabel() + " 시나리오에서는 " + productFit + " "
                    + businessSummary + " " + installation + " 현재는 확대 판단에 앞서 확인된 제한 사항부터 해소하는 편이 좋겠습니다.";
        }
        if ("Go".equals(verdict)) {
            return request.regionLabel() + "의 " + request.customerLabel() + " 시나리오에서는 " + productFit + " "
                    + request.marketOpportunity() + " " + businessSummary + " " + installation
                    + " 주요 기준과 실측 항목을 통과해 다음 단계 확대를 검토할 수 있습니다.";
        }
        return request.regionLabel() + "의 " + request.customerLabel() + " 시나리오에서는 " + productFit + " "
                + request.marketOpportunity() + " " + businessSummary + " " + installation
                + " 가능성은 있지만 남은 확인 사항을 파일럿과 실제 데이터로 검증한 뒤 확대를 결정하는 것이 좋겠습니다.";
    }

    private String buildNextAction(
            FinalAxRecommendationRequest request,
            FinalAxDecisionPolicy.Decision decision
    ) {
        if ("보완 필요".equals(decision.verdict())) {
            return decision.reasons().getFirst() + " 원인을 확인하고 개선 가능성을 검토해 보세요.";
        }
        if (!request.marketFitVerified()) {
            return "목표 고객 인터뷰나 시장 조사로 지역 수요와 제안 메시지의 적합성을 먼저 확인해 보세요.";
        }
        if (!request.pilotDataVerified()) {
            return "시범 매장과 비교 매장의 실제 전후 매출을 수집해 도입 효과를 확인해 보세요.";
        }
        if (!request.financialDataVerified()) {
            return "실제 견적과 운영비를 반영해 B2B ROI와 제조 원가·CAPEX 회수기간을 다시 계산해 보세요.";
        }
        if (!request.deliverySiteVerified()) {
            return "후보 현장의 출입문·엘리베이터·설치 벽면을 실측해 " + request.recommendedDeliveryMethod() + " 방식의 가능 여부를 확인해 보세요.";
        }
        if (request.roiPaybackPeriodMonths() != null
                && request.roiPaybackPeriodMonths().compareTo(BigDecimal.valueOf(24)) > 0) {
            return "파일럿에서 월 추가 매출과 운영비를 다시 확인해 B2B 투자 회수기간을 낮출 수 있는지 검토해 보세요.";
        }
        if (request.capexPaybackPeriodYears() != null && request.capexPaybackPeriodYears() > 5) {
            return "수율과 생산량 가정을 점검해 제조 CAPEX 회수기간을 개선할 여지가 있는지 확인해 보세요.";
        }
        if (request.pilotEffectPercentagePoint() < 5) {
            return "파일럿 운영 조건을 보완하고 비교 매장 대비 효과가 확대 기준에 도달하는지 재검증해 보세요.";
        }
        return "현재 확인된 결과를 기준으로 다음 확대 투자 단계와 적용 범위를 검토해 보세요.";
    }

    private String formatMonths(BigDecimal months) {
        return months == null ? "산정 불가" : format(months.doubleValue()) + "개월";
    }

    private String formatYears(Double years) {
        return years == null ? "산정 불가" : format(years) + "년";
    }

    private String formatCurrency(double value) {
        return String.format(Locale.ROOT, "%,.0f원", value);
    }

    private String format(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
