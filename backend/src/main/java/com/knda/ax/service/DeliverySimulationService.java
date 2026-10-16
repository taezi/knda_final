package com.knda.ax.service;

import com.knda.ax.dto.DeliveryMethodResult;
import com.knda.ax.dto.DeliverySimulationRequest;
import com.knda.ax.dto.DeliverySimulationResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DeliverySimulationService {

    private static final int PRODUCT_WIDTH = 2214;
    private static final int PRODUCT_HEIGHT = 1245;

    private static final List<DeliveryMethod> METHODS = List.of(
            new DeliveryMethod("완제품", 1, 2350, 1400, "현장 조립은 없지만 대형 포장 반입 제약이 큽니다."),
            new DeliveryMethod("2분할 모듈", 2, 1207, 1345, "현장 정렬과 접합 작업이 필요합니다."),
            new DeliveryMethod("4분할 모듈", 4, 654, 723, "반입성은 가장 높지만 모듈 정렬·색 균일도 검증이 필요합니다.")
    );

    public DeliverySimulationResponse simulate(DeliverySimulationRequest request) {
        List<DeliveryMethodResult> methods = METHODS.stream()
                .map(method -> evaluate(method, request))
                .toList();

        boolean wallInstallPossible = request.wallWidth() >= PRODUCT_WIDTH && request.wallHeight() >= PRODUCT_HEIGHT;
        Optional<DeliveryMethodResult> recommended = methods.stream()
                .filter(DeliveryMethodResult::deliveryPossible)
                .min(Comparator.comparing(DeliveryMethodResult::moduleCount));

        if (recommended.isEmpty()) {
            return new DeliverySimulationResponse(
                    PRODUCT_WIDTH,
                    PRODUCT_HEIGHT,
                    wallInstallPossible,
                    methods,
                    "반입 가능 방식 없음",
                    null,
                    "입력한 반입 경로에서는 모든 방식이 통과하지 못합니다. 현장 실측 또는 크레인·별도 반입 경로를 검토하세요."
            );
        }

        DeliveryMethodResult result = recommended.get();
        String recommendation = wallInstallPossible
                ? result.method() + " 방식을 권장합니다. 반입 가능한 방식 중 모듈 수가 가장 적어 현장 조립 부담을 줄일 수 있습니다."
                : result.method() + " 방식으로 반입은 가능하지만 설치 벽면이 100인치 제품 규격보다 작습니다. 설치 공간을 재검토하세요.";

        return new DeliverySimulationResponse(
                PRODUCT_WIDTH,
                PRODUCT_HEIGHT,
                wallInstallPossible,
                methods,
                result.method(),
                result.moduleCount(),
                recommendation
        );
    }

    private DeliveryMethodResult evaluate(DeliveryMethod method, DeliverySimulationRequest request) {
        boolean doorPass = fitsOpening(method.packageWidth(), method.packageHeight(), request.doorWidth(), request.doorHeight());
        boolean elevatorDoorPass = fitsOpening(method.packageWidth(), method.packageHeight(), request.elevatorDoorWidth(), request.elevatorDoorHeight());
        boolean elevatorCabinPass = fitsCabin(method.packageWidth(), method.packageHeight(), request.elevatorCabinWidth(), request.elevatorCabinDepth());
        boolean deliveryPossible = doorPass && elevatorDoorPass && elevatorCabinPass;

        return new DeliveryMethodResult(
                method.method(),
                method.moduleCount(),
                method.packageWidth(),
                method.packageHeight(),
                doorPass,
                elevatorDoorPass,
                elevatorCabinPass,
                deliveryPossible,
                method.note()
        );
    }

    private boolean fitsOpening(int packageWidth, int packageHeight, int openingWidth, int openingHeight) {
        return (packageWidth <= openingWidth && packageHeight <= openingHeight)
                || (packageHeight <= openingWidth && packageWidth <= openingHeight);
    }

    private boolean fitsCabin(int packageWidth, int packageHeight, int cabinWidth, int cabinDepth) {
        return (packageWidth <= cabinWidth && packageHeight <= cabinDepth)
                || (packageHeight <= cabinWidth && packageWidth <= cabinDepth);
    }

    private record DeliveryMethod(String method, int moduleCount, int packageWidth, int packageHeight, String note) {
    }
}
