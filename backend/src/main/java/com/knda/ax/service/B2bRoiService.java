package com.knda.ax.service;

import com.knda.ax.dto.B2bRoiRequest;
import com.knda.ax.dto.B2bRoiResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class B2bRoiService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWENTY_FOUR_MONTHS = BigDecimal.valueOf(24);
    private static final BigDecimal THIRTY_SIX_MONTHS = BigDecimal.valueOf(36);

    public B2bRoiResponse calculate(B2bRoiRequest request) {
        BigDecimal monthlyContributionProfit = request.monthlyAdditionalRevenue()
                .multiply(request.contributionMarginPercent())
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP)
                .subtract(request.monthlyOperatingCost())
                .setScale(2, RoundingMode.HALF_UP);

        if (monthlyContributionProfit.compareTo(BigDecimal.ZERO) <= 0) {
            return new B2bRoiResponse(
                    monthlyContributionProfit,
                    null,
                    "조건 재검토",
                    "월 추가 공헌이익이 0 이하이므로 매출 개선 또는 운영비 절감이 필요합니다."
            );
        }

        BigDecimal paybackPeriodMonths = request.totalInvestment()
                .divide(monthlyContributionProfit, 1, RoundingMode.HALF_UP);

        if (paybackPeriodMonths.compareTo(TWENTY_FOUR_MONTHS) <= 0) {
            return new B2bRoiResponse(
                    monthlyContributionProfit,
                    paybackPeriodMonths,
                    "확대 검토",
                    "목표 회수기간 24개월 이내입니다. 파일럿 성과와 설치 품질을 확인한 뒤 확대를 검토하세요."
            );
        }

        if (paybackPeriodMonths.compareTo(THIRTY_SIX_MONTHS) <= 0) {
            return new B2bRoiResponse(
                    monthlyContributionProfit,
                    paybackPeriodMonths,
                    "조건부 파일럿 권고",
                    "회수기간이 24~36개월 범위입니다. 시범 매장 데이터로 매출 개선 가능성을 검증하세요."
            );
        }

        return new B2bRoiResponse(
                monthlyContributionProfit,
                paybackPeriodMonths,
                "조건 재검토",
                "회수기간이 36개월을 초과합니다. 초기 투자비, 추가 매출 또는 운영비 가정을 재검토하세요."
        );
    }
}
