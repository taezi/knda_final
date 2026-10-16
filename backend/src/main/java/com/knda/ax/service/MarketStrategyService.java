package com.knda.ax.service;

import com.knda.ax.dto.MarketStrategyRequest;
import com.knda.ax.dto.MarketStrategyResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketStrategyService {

    private final NaverNewsService naverNewsService;

    public MarketStrategyService(NaverNewsService naverNewsService) {
        this.naverNewsService = naverNewsService;
    }

    public MarketStrategyResponse recommend(MarketStrategyRequest request) {
        RegionStrategy region = regionStrategyFor(request.region());
        CustomerStrategy customer = customerStrategyFor(request.customerSegment());
        String newsSearchQuery = region.newsSearchTerm() + " " + customer.newsSearchTerm();
        var newsResult = naverNewsService.searchRelevant(
                newsSearchQuery,
                List.of("OLED", "올레드", "TV", "100인치", "초대형", "디스플레이"),
                region.newsKeywords(),
                customer.newsKeywords()
        );

        return new MarketStrategyResponse(
                region.label(),
                customer.label(),
                region.opportunity(),
                customer.values(),
                customer.messagePrefix() + region.messageSuffix(),
                customer.channels(),
                newsSearchQuery,
                newsResult.articles(),
                newsResult.statusMessage()
        );
    }

    private RegionStrategy regionStrategyFor(String region) {
        return switch (region) {
            case "NORTH_AMERICA" -> new RegionStrategy(
                    "북미",
                    "대화면 프리미엄 TV와 홈 엔터테인먼트 수요가 집중된 핵심 공략 시장입니다.",
                    "낮과 밤을 가리지 않는 100인치 프리미엄 경험을 제안하세요.",
                    "미국",
                    List.of("미국", "북미", "미주", "usa", "us")
            );
            case "EUROPE" -> new RegionStrategy(
                    "유럽",
                    "프리미엄 디자인과 지속가능성, 고급 주거 공간 경험을 함께 강조하기 좋은 시장입니다.",
                    "공간의 품격을 완성하는 프리미엄 OLED 경험을 제안하세요.",
                    "유럽",
                    List.of("유럽", "영국", "독일", "프랑스", "EU")
            );
            case "KOREA" -> new RegionStrategy(
                    "한국",
                    "초대형 프리미엄 가전과 고급 인테리어 수요를 빠르게 검증할 수 있는 파일럿 시장입니다.",
                    "차별화된 초대형 OLED 경험으로 프리미엄 공간을 완성하세요.",
                    "국내",
                    List.of("한국", "국내", "K-", "코리아")
            );
            default -> throw new IllegalArgumentException("지원하지 않는 지역입니다.");
        };
    }

    private CustomerStrategy customerStrategyFor(String customerSegment) {
        return switch (customerSegment) {
            case "HOME_THEATER" -> new CustomerStrategy(
                    "B2C 하이엔드 홈시네마",
                    List.of("낮에도 유지되는 OLED 화질", "완전한 블랙 기반의 몰입감", "벽면을 완성하는 갤러리 디자인"),
                    "프라이빗 시네마를 원하는 고객에게, ",
                    List.of("프리미엄 가전 리테일", "고급 주거·인테리어 제휴", "홈시네마 전문 채널"),
                    "OLED 홈시네마",
                    List.of("홈시네마", "홈 시네마", "홈엔터테인먼트", "홈 엔터테인먼트", "시네마")
            );
            case "B2B_PREMIUM" -> new CustomerStrategy(
                    "B2B 프리미엄 공간",
                    List.of("공간 경험을 높이는 시그니처 스크린", "고객 체류·매출 개선 가능성", "프리미엄 브랜드 이미지"),
                    "프리미엄 고객 경험을 설계하는 기업에게, ",
                    List.of("호텔·럭셔리 리테일 직접 영업", "공간 설계사 제휴", "B2B 쇼룸 파일럿"),
                    "OLED 호텔 디스플레이",
                    List.of("호텔", "리테일", "쇼룸", "상업용", "B2B")
            );
            case "SPORTS_BAR" -> new CustomerStrategy(
                    "스포츠 펍·바 체인",
                    List.of("빠른 응답속도의 경기 몰입감", "밝은 매장에서도 선명한 화질", "체류시간·객단가 개선 가능성"),
                    "스포츠 관람 경험을 차별화하려는 매장에게, ",
                    List.of("체인 본사 직접 제안", "스포츠 이벤트 공동 프로모션", "대표 매장 파일럿"),
                    "OLED 스포츠바 TV",
                    List.of("스포츠바", "스포츠 바", "펍", "경기", "스포츠")
            );
            case "CASINO" -> new CustomerStrategy(
                    "카지노·통합 리조트",
                    List.of("VIP 존을 위한 압도적 화질", "고급 공간에 맞는 초슬림 디자인", "장시간 운영 환경의 프리미엄 경험"),
                    "VIP 고객 경험을 강화하려는 리조트에게, ",
                    List.of("리조트 운영사 직접 영업", "VIP 라운지 파일럿", "통합 AV 파트너 제휴"),
                    "OLED 카지노 리조트",
                    List.of("카지노", "리조트", "VIP", "라운지")
            );
            default -> throw new IllegalArgumentException("지원하지 않는 고객군입니다.");
        };
    }

    private record RegionStrategy(String label, String opportunity, String messageSuffix, String newsSearchTerm,
                                  List<String> newsKeywords) {
    }

    private record CustomerStrategy(String label, List<String> values, String messagePrefix, List<String> channels,
                                    String newsSearchTerm, List<String> newsKeywords) {
    }
}
