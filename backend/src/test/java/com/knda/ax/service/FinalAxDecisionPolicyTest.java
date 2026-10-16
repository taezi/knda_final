package com.knda.ax.service;

import com.knda.ax.dto.FinalAxRecommendationRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FinalAxDecisionPolicyTest {

    private final FinalAxDecisionPolicy policy = new FinalAxDecisionPolicy();

    @Test
    void currentExampleIsConditionalUntilActualInputsAreVerified() {
        FinalAxDecisionPolicy.Decision decision = policy.evaluate(request(
                "FLiPP OLED", 32.7, 7.3, 6.0, false, false, false, false, true, true, true
        ));

        assertThat(decision.verdict()).isEqualTo("조건부 Go");
        assertThat(decision.reasons()).anyMatch(reason -> reason.contains("24개월"));
        assertThat(decision.reasons()).anyMatch(reason -> reason.contains("5년"));
    }

    @Test
    void allGatesPassAndVerifiedDataAllowGo() {
        FinalAxDecisionPolicy.Decision decision = policy.evaluate(request(
                "FLiPP OLED", 20, 4.5, 6.0, true, true, true, true, true, true, true
        ));

        assertThat(decision.verdict()).isEqualTo("Go");
    }

    @Test
    void criticalFailureTakesPriorityOverConditionalItems() {
        FinalAxDecisionPolicy.Decision decision = policy.evaluate(request(
                "Mini-LED", 40, 9.0, 6.0, true, false, false, false, false, false, true
        ));

        assertThat(decision.verdict()).isEqualTo("보완 필요");
        assertThat(decision.reasons()).anyMatch(reason -> reason.contains("1순위"));
        assertThat(decision.reasons()).anyMatch(reason -> reason.contains("36개월"));
    }

    @Test
    void verifiedNonPositivePilotEffectBlocksExpansion() {
        FinalAxDecisionPolicy.Decision decision = policy.evaluate(request(
                "FLiPP OLED", 20, 4.5, 0.0, true, true, true, true, true, true, true
        ));

        assertThat(decision.verdict()).isEqualTo("보완 필요");
        assertThat(decision.reasons()).anyMatch(reason -> reason.contains("실측 파일럿"));
    }

    private FinalAxRecommendationRequest request(
            String product,
            double roiMonths,
            double capexYears,
            double pilotEffect,
            boolean pilotVerified,
            boolean marketVerified,
            boolean financialVerified,
            boolean siteVerified,
            boolean routePossible,
            boolean wallPossible,
            boolean profitable
    ) {
        return new FinalAxRecommendationRequest(
                "북미", "B2C 하이엔드 홈시네마", "화질", product, "시장 기회",
                profitable ? BigDecimal.valueOf(roiMonths) : null,
                profitable ? BigDecimal.valueOf(100_000) : BigDecimal.valueOf(-100),
                "확대 검토", pilotEffect, pilotVerified, marketVerified, "파일럿 검증",
                500_000.0, capexYears, wallPossible, routePossible,
                financialVerified, siteVerified, "4분할 모듈"
        );
    }
}
