package com.knda.ax.service;

import com.knda.ax.dto.ManufacturerFeasibilityRequest;
import com.knda.ax.dto.ManufacturerFeasibilityResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ManufacturerFeasibilityService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal FIVE_YEARS = BigDecimal.valueOf(5);
    private static final BigDecimal EIGHT_YEARS = BigDecimal.valueOf(8);

    public ManufacturerFeasibilityResponse analyze(ManufacturerFeasibilityRequest request) {
        BigDecimal ommBom = request.ommPanelCost()
                .add(request.componentAndAssemblyCost())
                .add(request.logisticsAndPackagingCost());

        BigDecimal directCostFactor = BigDecimal.ONE.subtract(
                request.fippDirectCostReductionPercent().divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP)
        );
        BigDecimal yieldCorrectionFactor = request.ommYieldPercent()
                .divide(request.fippYieldPercent(), 4, RoundingMode.HALF_UP);
        BigDecimal fippPanelCost = request.ommPanelCost()
                .multiply(directCostFactor)
                .multiply(yieldCorrectionFactor)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal fippBom = fippPanelCost
                .add(request.componentAndAssemblyCost())
                .add(request.logisticsAndPackagingCost());
        BigDecimal perUnitSavings = ommBom.subtract(fippBom);
        BigDecimal annualSavings = perUnitSavings.multiply(BigDecimal.valueOf(request.annualProductionVolume()));
        BigDecimal manufacturingCostRatio = fippBom
                .divide(request.sellingPrice(), 4, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED)
                .setScale(1, RoundingMode.HALF_UP);

        if (annualSavings.compareTo(BigDecimal.ZERO) <= 0) {
            return new ManufacturerFeasibilityResponse(
                    ommBom, fippPanelCost, fippBom, perUnitSavings, annualSavings, manufacturingCostRatio, null,
                    "조건 재검토",
                    "FLiPP 전환 후 원가 절감 효과가 없습니다. 수율 또는 직접 원가절감 가정을 재검토하세요."
            );
        }

        BigDecimal paybackPeriodYears = request.capex()
                .divide(annualSavings, 1, RoundingMode.HALF_UP);

        if (paybackPeriodYears.compareTo(FIVE_YEARS) <= 0) {
            return new ManufacturerFeasibilityResponse(
                    ommBom, fippPanelCost, fippBom, perUnitSavings, annualSavings, manufacturingCostRatio, paybackPeriodYears,
                    "단계적 양산 투자 권고",
                    "원가 절감과 생산량 가정 기준으로 CAPEX 회수기간이 5년 이내입니다. 수율 검증을 병행하며 양산 투자를 검토하세요."
            );
        }

        if (paybackPeriodYears.compareTo(EIGHT_YEARS) <= 0) {
            return new ManufacturerFeasibilityResponse(
                    ommBom, fippPanelCost, fippBom, perUnitSavings, annualSavings, manufacturingCostRatio, paybackPeriodYears,
                    "파일럿 라인 우선 추진",
                    "회수기간이 5~8년 범위입니다. 파일럿 라인에서 수율과 원가 가정을 검증한 뒤 투자 규모를 결정하세요."
            );
        }

        return new ManufacturerFeasibilityResponse(
                ommBom, fippPanelCost, fippBom, perUnitSavings, annualSavings, manufacturingCostRatio, paybackPeriodYears,
                "조건 재검토",
                "회수기간이 8년을 초과합니다. CAPEX 절감, 생산량 확대 또는 판매가 전략을 재검토하세요."
        );
    }
}
