package com.knda.ax.service;

import com.knda.ax.dto.FinalAxRecommendationRequest;
import com.knda.ax.dto.FinalAxRecommendationResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ScenarioAxRecommendationServiceTest {

    private final ScenarioAxRecommendationService service =
            new ScenarioAxRecommendationService(new FinalAxDecisionPolicy());

    @Test
    void buildsScenarioRecommendationWithoutCodexAndUsesPolicyVerdict() {
        FinalAxRecommendationResponse response = service.recommend(new FinalAxRecommendationRequest(
                "북미", "B2C 하이엔드 홈시네마", "화질", "FLiPP OLED", "프리미엄 홈시네마 수요",
                BigDecimal.valueOf(32.7), BigDecimal.valueOf(1_225), "조건부 파일럿 권고",
                6.0, false, false, "파일럿 검증", 500_000.0, 7.3,
                true, true, false, false, "4분할 모듈"
        ));

        assertThat(response.generatedByCodex()).isFalse();
        assertThat(response.verdict()).isEqualTo("조건부 Go");
        assertThat(response.recommendation()).contains("북미", "32.7개월", "가정", "7.3년", "4분할 모듈");
        assertThat(response.nextAction()).contains("고객 인터뷰");
        assertThat(response.decisionReasons()).anyMatch(reason -> reason.contains("가정값"));
    }
}
