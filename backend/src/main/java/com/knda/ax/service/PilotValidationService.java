package com.knda.ax.service;

import com.knda.ax.dto.PilotValidationRequest;
import com.knda.ax.dto.PilotValidationResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PilotValidationService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal EXPANSION_THRESHOLD = BigDecimal.valueOf(5);

    public PilotValidationResponse analyze(PilotValidationRequest request) {
        BigDecimal pilotChangePercent = calculateChangePercent(request.pilotBeforeSales(), request.pilotAfterSales());
        BigDecimal controlChangePercent = calculateChangePercent(request.controlBeforeSales(), request.controlAfterSales());
        BigDecimal estimatedEffect = pilotChangePercent.subtract(controlChangePercent).setScale(1, RoundingMode.HALF_UP);

        if (estimatedEffect.compareTo(EXPANSION_THRESHOLD) >= 0) {
            return new PilotValidationResponse(
                    pilotChangePercent,
                    controlChangePercent,
                    estimatedEffect,
                    "확대 검토",
                    "시범 매장 개선폭이 비교 매장보다 5%p 이상 높습니다. ROI와 설치 품질을 함께 확인한 뒤 추가 지점 확대를 검토하세요."
            );
        }

        if (estimatedEffect.compareTo(BigDecimal.ZERO) > 0) {
            return new PilotValidationResponse(
                    pilotChangePercent,
                    controlChangePercent,
                    estimatedEffect,
                    "개선 후 재검증",
                    "긍정적 효과는 관측됐지만 확대 기준에는 미달합니다. 가격, 구성 또는 설치 환경을 개선한 뒤 다시 검증하세요."
            );
        }

        return new PilotValidationResponse(
                pilotChangePercent,
                controlChangePercent,
                estimatedEffect,
                "재선정·중단",
                "시범 매장의 개선폭이 비교 매장보다 높지 않습니다. 타깃 고객 또는 설치 환경을 재검토하세요."
        );
    }

    private BigDecimal calculateChangePercent(BigDecimal beforeSales, BigDecimal afterSales) {
        return afterSales.subtract(beforeSales)
                .divide(beforeSales, 4, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED)
                .setScale(1, RoundingMode.HALF_UP);
    }
}
