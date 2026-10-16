package com.knda.ax.service;

import com.knda.ax.dto.FinalAxRecommendationRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Applies the project-approved deterministic gates before Codex writes the explanation. */
@Component
public class FinalAxDecisionPolicy {

    private static final BigDecimal ROI_GREEN_MONTHS = BigDecimal.valueOf(24);
    private static final BigDecimal ROI_RED_MONTHS = BigDecimal.valueOf(36);
    private static final BigDecimal CAPEX_GREEN_YEARS = BigDecimal.valueOf(5);
    private static final BigDecimal CAPEX_RED_YEARS = BigDecimal.valueOf(8);
    private static final BigDecimal PILOT_EXPANSION_EFFECT_PP = BigDecimal.valueOf(5);

    public Decision evaluate(FinalAxRecommendationRequest request) {
        List<String> blockers = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (!request.recommendedProduct().toLowerCase().contains("flipp")) {
            blockers.add("선택한 고객 조건에서 FLiPP OLED가 경쟁력 비교 1순위가 아닙니다.");
        }
        if (!request.marketFitVerified()) {
            conditions.add("목표 고객의 수요와 제안 메시지가 실제 고객 인터뷰·시장 조사로 검증되지 않았습니다.");
        }

        BigDecimal monthlyProfit = request.monthlyContributionProfit();
        if (monthlyProfit.compareTo(BigDecimal.ZERO) <= 0) {
            blockers.add("B2B 월 추가 공헌이익이 0 이하입니다.");
        } else if (request.roiPaybackPeriodMonths() == null) {
            conditions.add("B2B 투자 회수기간을 산정할 수 없어 확인이 필요합니다.");
        } else {
            BigDecimal roiMonths = request.roiPaybackPeriodMonths();
            if (roiMonths.compareTo(ROI_RED_MONTHS) > 0) {
                blockers.add("B2B 투자 회수기간이 36개월을 초과합니다.");
            } else if (roiMonths.compareTo(ROI_GREEN_MONTHS) > 0) {
                conditions.add("B2B 투자 회수기간이 24개월을 넘어 파일럿 검증이 필요합니다.");
            }
        }

        BigDecimal pilotEffect = BigDecimal.valueOf(request.pilotEffectPercentagePoint());
        if (request.pilotDataVerified() && pilotEffect.compareTo(BigDecimal.ZERO) <= 0) {
            blockers.add("실측 파일럿에서 비교 매장 대비 개선 효과가 확인되지 않았습니다.");
        } else if (!request.pilotDataVerified()) {
            conditions.add("파일럿 효과가 가정값입니다. 실제 매장 데이터로 검증해야 합니다.");
        } else if (pilotEffect.compareTo(PILOT_EXPANSION_EFFECT_PP) < 0) {
            conditions.add("실측 파일럿 효과가 확대 기준인 +5%p에 미달합니다.");
        }

        if (request.perUnitSavings() <= 0) {
            blockers.add("FLiPP 전환에 따른 대당 원가 절감이 없습니다.");
        }

        if (request.capexPaybackPeriodYears() == null) {
            conditions.add("제조 CAPEX 회수기간을 산정할 수 없어 확인이 필요합니다.");
        } else {
            BigDecimal capexYears = BigDecimal.valueOf(request.capexPaybackPeriodYears());
            if (capexYears.compareTo(CAPEX_RED_YEARS) > 0) {
                blockers.add("제조 CAPEX 회수기간이 8년을 초과합니다.");
            } else if (capexYears.compareTo(CAPEX_GREEN_YEARS) > 0) {
                conditions.add("제조 CAPEX 회수기간이 5년을 넘어 단계적 검증이 필요합니다.");
            }
        }

        if (!request.wallInstallPossible()) {
            blockers.add("설치 벽면이 제품 규격을 충족하지 않습니다.");
        }
        if (!request.deliveryRoutePossible()) {
            blockers.add("입력한 반입 경로에서 가능한 조립 방식이 없습니다.");
        } else if (!request.deliverySiteVerified()) {
            conditions.add("반입·조립 가능성이 규격 시뮬레이션 단계이며 현장 실측이 남아 있습니다.");
        }

        if (!request.financialDataVerified()) {
            conditions.add("ROI와 제조 원가·CAPEX 입력값이 실제 견적·운영 데이터로 확인되지 않았습니다.");
        }

        if (!blockers.isEmpty()) {
            return new Decision("보완 필요", List.copyOf(blockers));
        }
        if (!conditions.isEmpty()) {
            return new Decision("조건부 Go", List.copyOf(conditions));
        }
        return new Decision("Go", List.of("시장·제품·수익성·제조·설치 기준을 모두 통과했고, 파일럿과 입력 데이터가 실측으로 확인됐습니다."));
    }

    public record Decision(String verdict, List<String> reasons) {
    }
}
