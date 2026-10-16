import { useEffect, useState } from "react";
import "./App.css";

const initialInputs = {
  totalInvestment: 40000000,
  monthlyAdditionalRevenue: 8500000,
  contributionMarginPercent: 25,
  monthlyOperatingCost: 900000,
};

const inputFields = [
  {
    key: "totalInvestment",
    label: "OLED 총 투자비",
    min: 10000000,
    max: 80000000,
    step: 1000000,
    suffix: "원",
  },
  {
    key: "monthlyAdditionalRevenue",
    label: "월 추가 매출",
    min: 1000000,
    max: 20000000,
    step: 500000,
    suffix: "원",
  },
  {
    key: "contributionMarginPercent",
    label: "공헌이익률",
    min: 5,
    max: 60,
    step: 1,
    suffix: "%",
  },
  {
    key: "monthlyOperatingCost",
    label: "월 추가 운영비",
    min: 0,
    max: 3000000,
    step: 100000,
    suffix: "원",
  },
];

const initialPilotInputs = {
  pilotStoreCount: 3,
  controlStoreCount: 3,
  pilotBeforeSales: 50000000,
  pilotAfterSales: 55000000,
  controlBeforeSales: 48000000,
  controlAfterSales: 49920000,
};

const pilotSalesFields = [
  {
    key: "pilotBeforeSales",
    label: "시범 매장 설치 전 월평균 매출",
    min: 10000000,
    max: 100000000,
    step: 1000000,
  },
  {
    key: "pilotAfterSales",
    label: "시범 매장 설치 후 월평균 매출",
    min: 10000000,
    max: 100000000,
    step: 1000000,
  },
  {
    key: "controlBeforeSales",
    label: "비교 매장 설치 전 월평균 매출",
    min: 10000000,
    max: 100000000,
    step: 1000000,
  },
  {
    key: "controlAfterSales",
    label: "비교 매장 설치 후 월평균 매출",
    min: 10000000,
    max: 100000000,
    step: 1000000,
  },
];

const customerOptions = [
  { value: "HOME_THEATER", label: "B2C 하이엔드 홈시네마" },
  { value: "B2B_PREMIUM", label: "B2B 프리미엄 공간" },
  { value: "SPORTS_BAR", label: "스포츠 펍·바 체인" },
  { value: "CASINO", label: "카지노·통합 리조트" },
];

const regionOptions = [
  { value: "NORTH_AMERICA", label: "북미" },
  { value: "EUROPE", label: "유럽" },
  { value: "KOREA", label: "한국" },
];

const priorityOptions = [
  { value: "QUALITY", label: "화질 중심" },
  { value: "PRICE", label: "가격 중심" },
  { value: "INSTALLATION", label: "설치 편의 중심" },
  { value: "DESIGN", label: "디자인 중심" },
];

const initialDeliveryInputs = {
  doorWidth: 950,
  doorHeight: 2100,
  elevatorDoorWidth: 900,
  elevatorDoorHeight: 2000,
  elevatorCabinWidth: 1200,
  elevatorCabinDepth: 1600,
  wallWidth: 3000,
  wallHeight: 2400,
};

const deliveryFields = [
  { key: "doorWidth", label: "출입문 폭", min: 600, max: 2000, step: 50 },
  { key: "doorHeight", label: "출입문 높이", min: 1800, max: 2800, step: 50 },
  {
    key: "elevatorDoorWidth",
    label: "엘리베이터 문 폭",
    min: 600,
    max: 1800,
    step: 50,
  },
  {
    key: "elevatorDoorHeight",
    label: "엘리베이터 문 높이",
    min: 1800,
    max: 2600,
    step: 50,
  },
  {
    key: "elevatorCabinWidth",
    label: "엘리베이터 내부 폭",
    min: 800,
    max: 2500,
    step: 50,
  },
  {
    key: "elevatorCabinDepth",
    label: "엘리베이터 내부 깊이",
    min: 800,
    max: 2500,
    step: 50,
  },
  { key: "wallWidth", label: "설치 벽면 폭", min: 1500, max: 5000, step: 100 },
  {
    key: "wallHeight",
    label: "설치 벽면 높이",
    min: 1000,
    max: 4000,
    step: 100,
  },
];

const initialManufacturerInputs = {
  ommPanelCost: 3850000,
  ommYieldPercent: 75,
  fippYieldPercent: 93,
  fippDirectCostReductionPercent: 20,
  componentAndAssemblyCost: 1400000,
  logisticsAndPackagingCost: 700000,
  annualProductionVolume: 10000,
  capex: 100000000000,
  sellingPrice: 16000000,
};

const manufacturerFields = [
  {
    key: "ommPanelCost",
    label: "OMM 패널 원가",
    min: 2500000,
    max: 5000000,
    step: 100000,
    format: "won",
  },
  {
    key: "ommYieldPercent",
    label: "OMM 수율",
    min: 50,
    max: 95,
    step: 1,
    format: "percent",
  },
  {
    key: "fippYieldPercent",
    label: "FLiPP 목표 수율",
    min: 50,
    max: 98,
    step: 1,
    format: "percent",
  },
  {
    key: "fippDirectCostReductionPercent",
    label: "FLiPP 직접 원가절감률",
    min: 0,
    max: 50,
    step: 1,
    format: "percent",
  },
  {
    key: "componentAndAssemblyCost",
    label: "기타 부품·조립비",
    min: 500000,
    max: 3000000,
    step: 100000,
    format: "won",
  },
  {
    key: "logisticsAndPackagingCost",
    label: "물류·패키징비",
    min: 200000,
    max: 1500000,
    step: 100000,
    format: "won",
  },
  {
    key: "annualProductionVolume",
    label: "연간 생산량",
    min: 1000,
    max: 100000,
    step: 1000,
    format: "volume",
  },
  {
    key: "capex",
    label: "FLiPP 초기 투자비(CAPEX)",
    min: 50000000000,
    max: 200000000000,
    step: 10000000000,
    format: "won",
  },
  {
    key: "sellingPrice",
    label: "예상 판매가",
    min: 10000000,
    max: 25000000,
    step: 500000,
    format: "won",
  },
];

const formatCurrency = (value) =>
  `₩${Number(value).toLocaleString("ko-KR", { maximumFractionDigits: 0 })}`;
const formatPercent = (value) =>
  `${Number(value) > 0 ? "+" : ""}${Number(value).toFixed(1)}%`;
const formatManufacturerValue = (field, value) => {
  if (field.format === "percent") return `${value}%`;
  if (field.format === "volume")
    return `${Number(value).toLocaleString("ko-KR")}대`;
  return formatCurrency(value);
};

const moduleTitles = {
  summary: "통합 의사결정 요약",
  roi: "B2B ROI Simulator",
  pilot: "파일럿 매장 성과 검증 Tool",
  delivery: "100인치 OLED 반입·조립 시뮬레이터",
  competition: "경쟁력 비교 Tool",
  marketStrategy: "시장·고객 전략 추천 Tool",
  manufacturer: "100인치 OLED 사업성 시뮬레이터",
};

function App() {
  const [inputs, setInputs] = useState(initialInputs);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [pilotInputs, setPilotInputs] = useState(initialPilotInputs);
  const [pilotResult, setPilotResult] = useState(null);
  const [pilotError, setPilotError] = useState("");
  const [scenarioInputs, setScenarioInputs] = useState({
    region: "NORTH_AMERICA",
    customerSegment: "HOME_THEATER",
    priority: "QUALITY",
  });
  const [scenarioDraft, setScenarioDraft] = useState(scenarioInputs);
  const [isScenarioEditorOpen, setIsScenarioEditorOpen] = useState(false);
  const [competitionResult, setCompetitionResult] = useState(null);
  const [competitionError, setCompetitionError] = useState("");
  const [marketStrategyResult, setMarketStrategyResult] = useState(null);
  const [marketStrategyError, setMarketStrategyError] = useState("");
  const [deliveryInputs, setDeliveryInputs] = useState(initialDeliveryInputs);
  const [deliveryResult, setDeliveryResult] = useState(null);
  const [deliveryError, setDeliveryError] = useState("");
  const [manufacturerInputs, setManufacturerInputs] = useState(
    initialManufacturerInputs,
  );
  const [manufacturerResult, setManufacturerResult] = useState(null);
  const [manufacturerError, setManufacturerError] = useState("");
  const [finalRecommendation, setFinalRecommendation] = useState(null);
  const [finalRecommendationError, setFinalRecommendationError] = useState("");
  const [isGeneratingFinalRecommendation, setIsGeneratingFinalRecommendation] =
    useState(false);
  const [selectedModule, setSelectedModule] = useState("summary");

  useEffect(() => {
    setFinalRecommendation(null);
    setFinalRecommendationError("");
  }, [inputs, pilotInputs, scenarioInputs, deliveryInputs, manufacturerInputs]);

  useEffect(() => {
    if (!isScenarioEditorOpen) return undefined;

    function closeOnEscape(event) {
      if (event.key === "Escape") setIsScenarioEditorOpen(false);
    }

    window.addEventListener("keydown", closeOnEscape);
    return () => window.removeEventListener("keydown", closeOnEscape);
  }, [isScenarioEditorOpen]);

  useEffect(() => {
    const controller = new AbortController();

    async function calculateRoi() {
      try {
        setError("");
        const response = await fetch(
          "http://localhost:8080/api/b2b-roi/calculate",
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(inputs),
            signal: controller.signal,
          },
        );

        if (!response.ok) {
          throw new Error("ROI 계산 요청에 실패했습니다.");
        }

        setResult(await response.json());
      } catch (requestError) {
        if (requestError.name !== "AbortError") {
          setError(
            "백엔드 연결이 필요합니다. 8080 포트의 Spring Boot 서버를 실행해 주세요.",
          );
          setResult(null);
        }
      }
    }

    calculateRoi();
    return () => controller.abort();
  }, [inputs]);

  useEffect(() => {
    const controller = new AbortController();

    async function analyzePilot() {
      try {
        setPilotError("");
        const response = await fetch(
          "http://localhost:8080/api/pilot-validation/analyze",
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(pilotInputs),
            signal: controller.signal,
          },
        );

        if (!response.ok) {
          throw new Error("파일럿 성과 검증 요청에 실패했습니다.");
        }

        setPilotResult(await response.json());
      } catch (requestError) {
        if (requestError.name !== "AbortError") {
          setPilotError(
            "백엔드 연결이 필요합니다. 8080 포트의 Spring Boot 서버를 실행해 주세요.",
          );
          setPilotResult(null);
        }
      }
    }

    analyzePilot();
    return () => controller.abort();
  }, [pilotInputs]);

  useEffect(() => {
    const controller = new AbortController();

    async function compareProducts() {
      try {
        setCompetitionError("");
        const response = await fetch(
          "http://localhost:8080/api/competitiveness/compare",
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              customerSegment: scenarioInputs.customerSegment,
              priority: scenarioInputs.priority,
            }),
            signal: controller.signal,
          },
        );

        if (!response.ok) {
          throw new Error("경쟁력 비교 요청에 실패했습니다.");
        }

        setCompetitionResult(await response.json());
      } catch (requestError) {
        if (requestError.name !== "AbortError") {
          setCompetitionError(
            "백엔드 연결이 필요합니다. 8080 포트의 Spring Boot 서버를 실행해 주세요.",
          );
          setCompetitionResult(null);
        }
      }
    }

    compareProducts();
    return () => controller.abort();
  }, [scenarioInputs.customerSegment, scenarioInputs.priority]);

  useEffect(() => {
    const controller = new AbortController();

    async function recommendMarketStrategy() {
      try {
        setMarketStrategyError("");
        const response = await fetch(
          "http://localhost:8080/api/market-strategy/recommend",
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              region: scenarioInputs.region,
              customerSegment: scenarioInputs.customerSegment,
            }),
            signal: controller.signal,
          },
        );

        if (!response.ok) {
          throw new Error("시장·고객 전략 추천 요청에 실패했습니다.");
        }

        setMarketStrategyResult(await response.json());
      } catch (requestError) {
        if (requestError.name !== "AbortError") {
          setMarketStrategyError(
            "백엔드 연결이 필요합니다. 8080 포트의 Spring Boot 서버를 실행해 주세요.",
          );
          setMarketStrategyResult(null);
        }
      }
    }

    recommendMarketStrategy();
    return () => controller.abort();
  }, [scenarioInputs.region, scenarioInputs.customerSegment]);

  useEffect(() => {
    const controller = new AbortController();

    async function analyzeDelivery() {
      try {
        setDeliveryError("");
        const response = await fetch(
          "http://localhost:8080/api/delivery-simulation/analyze",
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(deliveryInputs),
            signal: controller.signal,
          },
        );

        if (!response.ok) {
          throw new Error("반입·조립 시뮬레이션 요청에 실패했습니다.");
        }

        setDeliveryResult(await response.json());
      } catch (requestError) {
        if (requestError.name !== "AbortError") {
          setDeliveryError(
            "백엔드 연결이 필요합니다. 8080 포트의 Spring Boot 서버를 실행해 주세요.",
          );
          setDeliveryResult(null);
        }
      }
    }

    analyzeDelivery();
    return () => controller.abort();
  }, [deliveryInputs]);

  useEffect(() => {
    const controller = new AbortController();

    async function analyzeManufacturerFeasibility() {
      try {
        setManufacturerError("");
        const response = await fetch(
          "http://localhost:8080/api/manufacturer-feasibility/analyze",
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(manufacturerInputs),
            signal: controller.signal,
          },
        );

        if (!response.ok) {
          throw new Error("제조사 사업성 분석 요청에 실패했습니다.");
        }

        setManufacturerResult(await response.json());
      } catch (requestError) {
        if (requestError.name !== "AbortError") {
          setManufacturerError(
            "백엔드 연결이 필요합니다. 8080 포트의 Spring Boot 서버를 실행해 주세요.",
          );
          setManufacturerResult(null);
        }
      }
    }

    analyzeManufacturerFeasibility();
    return () => controller.abort();
  }, [manufacturerInputs]);

  function updateInput(key, value) {
    setInputs((current) => ({ ...current, [key]: Number(value) }));
  }

  function updatePilotInput(key, value) {
    setPilotInputs((current) => ({
      ...current,
      [key]: typeof value === "boolean" ? value : Number(value),
    }));
  }

  function updateScenarioInput(key, value) {
    setScenarioInputs((current) => ({ ...current, [key]: value }));
  }

  function openScenarioEditor() {
    setScenarioDraft(scenarioInputs);
    setIsScenarioEditorOpen(true);
  }

  function updateScenarioDraft(key, value) {
    setScenarioDraft((current) => ({ ...current, [key]: value }));
  }

  function applyScenarioDraft() {
    setScenarioInputs(scenarioDraft);
    setIsScenarioEditorOpen(false);
  }

  function updateDeliveryInput(key, value) {
    setDeliveryInputs((current) => ({ ...current, [key]: Number(value) }));
  }

  function updateManufacturerInput(key, value) {
    setManufacturerInputs((current) => ({ ...current, [key]: Number(value) }));
  }

  async function generateFinalRecommendation(generator = "codex") {
    if (
      !competitionResult ||
      !marketStrategyResult ||
      !result ||
      !pilotResult ||
      !manufacturerResult ||
      !deliveryResult
    ) {
      return;
    }

    try {
      setIsGeneratingFinalRecommendation(true);
      setFinalRecommendationError("");
      setFinalRecommendation(null);
      const response = await fetch(
        generator === "codex"
          ? "http://localhost:8080/api/final-ax-recommendation/generate"
          : "http://localhost:8080/api/final-ax-recommendation/scenario",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            regionLabel: selectedRegion,
            customerLabel: selectedCustomer,
            priorityLabel: selectedPriority,
            recommendedProduct: competitionResult.recommendedProduct,
            marketOpportunity: marketStrategyResult.marketOpportunity,
            roiPaybackPeriodMonths: result.paybackPeriodMonths,
            monthlyContributionProfit: result.monthlyContributionProfit,
            roiRecommendation: result.recommendation,
            pilotEffectPercentagePoint:
              pilotResult.estimatedEffectPercentagePoint,
            pilotDataVerified: true,
            marketFitVerified: true,
            pilotRecommendation: pilotResult.recommendation,
            perUnitSavings: manufacturerResult.perUnitSavings,
            capexPaybackPeriodYears: manufacturerResult.paybackPeriodYears,
            wallInstallPossible: deliveryResult.wallInstallPossible,
            deliveryRoutePossible: deliveryResult.methods.some(
              (method) => method.deliveryPossible,
            ),
            financialDataVerified: true,
            deliverySiteVerified: true,
            recommendedDeliveryMethod: deliveryResult.recommendedMethod,
          }),
        },
      );

      if (!response.ok) {
        const errorBody = await response.json().catch(() => null);
        throw new Error(
          errorBody?.detail ||
            errorBody?.message ||
            "최종 AX 권고 생성 요청에 실패했습니다.",
        );
      }

      const recommendation = await response.json();
      setFinalRecommendation(recommendation);
    } catch (requestError) {
      setFinalRecommendation(null);
      setFinalRecommendationError(
        requestError.message ||
          "Codex 연결에 실패했습니다. 잠시 후 다시 시도해 주세요.",
      );
    } finally {
      setIsGeneratingFinalRecommendation(false);
    }
  }

  const selectedRegion = regionOptions.find(
    (option) => option.value === scenarioInputs.region,
  )?.label;
  const selectedCustomer = customerOptions.find(
    (option) => option.value === scenarioInputs.customerSegment,
  )?.label;
  const selectedPriority = priorityOptions.find(
    (option) => option.value === scenarioInputs.priority,
  )?.label;

  return (
    <main className="app-shell">
      <aside className="sidebar" aria-label="AX 기능 메뉴">
        <div>
          <div className="brand-lockup" aria-label="LGDashboard">
            <span className="brand-lgd">LGD</span>
            <span className="brand-ashboard">ashboard</span>
          </div>
          <p className="eyebrow">100&quot; OLED · AX</p>
          <h2>
            의사결정
            <br />
            지원 플랫폼
          </h2>
        </div>

        <nav className="sidebar-nav">
          <div className="sidebar-category">
            <p>영업</p>
            <button
              type="button"
              className={selectedModule === "roi" ? "active" : ""}
              onClick={() => setSelectedModule("roi")}
            >
              B2B ROI
            </button>
            <button
              type="button"
              className={selectedModule === "pilot" ? "active" : ""}
              onClick={() => setSelectedModule("pilot")}
            >
              파일럿 성과 검증
            </button>
            <button
              type="button"
              className={selectedModule === "delivery" ? "active" : ""}
              onClick={() => setSelectedModule("delivery")}
            >
              반입·조립
            </button>
          </div>
          <div className="sidebar-category">
            <p>마케팅</p>
            <button
              type="button"
              className={selectedModule === "competition" ? "active" : ""}
              onClick={() => setSelectedModule("competition")}
            >
              경쟁력 비교
            </button>
            <button
              type="button"
              className={selectedModule === "marketStrategy" ? "active" : ""}
              onClick={() => setSelectedModule("marketStrategy")}
            >
              시장·고객 전략
            </button>
          </div>
          <div className="sidebar-category">
            <p>R&amp;D · 공정</p>
            <button
              type="button"
              className={selectedModule === "manufacturer" ? "active" : ""}
              onClick={() => setSelectedModule("manufacturer")}
            >
              사업성 시뮬레이터
            </button>
          </div>
          <div className="sidebar-category">
            <p>통합 판단</p>
            <button
              type="button"
              className={selectedModule === "summary" ? "active" : ""}
              onClick={() => setSelectedModule("summary")}
            >
              의사결정 요약 대시보드
            </button>
          </div>
        </nav>
      </aside>

      <div className="dashboard-content">
        <header className="topbar">
          <div>
            <p className="eyebrow">100&quot; OLED · AX DECISION PLATFORM</p>
            <h1>{moduleTitles[selectedModule]}</h1>
          </div>
        </header>

        {isScenarioEditorOpen && (
          <div
            className="scenario-editor-overlay"
            onMouseDown={() => setIsScenarioEditorOpen(false)}
          >
            <section
              className="scenario-editor-dialog"
              role="dialog"
              aria-modal="true"
              aria-labelledby="scenario-editor-title"
              onMouseDown={(event) => event.stopPropagation()}
            >
              <div className="scenario-editor-heading">
                <div>
                  <p className="eyebrow">공통 시나리오</p>
                  <h2 id="scenario-editor-title">분석 조건 변경</h2>
                  <p>변경 내용을 적용하면 시장 전략과 경쟁력 결과가 함께 갱신됩니다.</p>
                </div>
                <button
                  type="button"
                  className="scenario-editor-close"
                  aria-label="시나리오 편집 창 닫기"
                  onClick={() => setIsScenarioEditorOpen(false)}
                >
                  ×
                </button>
              </div>

              <div className="scenario-modal-grid">
                <label>
                  공략 지역
                  <select
                    value={scenarioDraft.region}
                    onChange={(event) =>
                      updateScenarioDraft("region", event.target.value)
                    }
                  >
                    {regionOptions.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </label>
                <label>
                  핵심 고객군
                  <select
                    value={scenarioDraft.customerSegment}
                    onChange={(event) =>
                      updateScenarioDraft("customerSegment", event.target.value)
                    }
                  >
                    {customerOptions.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </label>
                <label>
                  구매 우선순위
                  <select
                    value={scenarioDraft.priority}
                    onChange={(event) =>
                      updateScenarioDraft("priority", event.target.value)
                    }
                  >
                    {priorityOptions.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </label>
              </div>

              <div className="scenario-editor-actions">
                <button
                  type="button"
                  className="scenario-cancel-button"
                  onClick={() => setIsScenarioEditorOpen(false)}
                >
                  취소
                </button>
                <button type="button" onClick={applyScenarioDraft}>
                  변경 적용
                </button>
              </div>
            </section>
          </div>
        )}

        <section
          className={`department-group summary-group ${selectedModule === "summary" ? "" : "department-hidden"}`}
        >
          <section className="summary-card" aria-labelledby="summary-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow"></p>
                <h2 id="summary-title">통합 의사결정 요약 대시보드</h2>
                <p>
                  여기서 선택한 하나의 시나리오를 경쟁력 비교와 시장·고객 전략에
                  함께 적용합니다.
                </p>
              </div>
            </div>

            <div className="select-grid scenario-select-grid">
              <label>
                공략 지역
                <select
                  value={scenarioInputs.region}
                  onChange={(event) =>
                    updateScenarioInput("region", event.target.value)
                  }
                >
                  {regionOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                핵심 고객군
                <select
                  value={scenarioInputs.customerSegment}
                  onChange={(event) =>
                    updateScenarioInput("customerSegment", event.target.value)
                  }
                >
                  {customerOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                구매 우선순위
                <select
                  value={scenarioInputs.priority}
                  onChange={(event) =>
                    updateScenarioInput("priority", event.target.value)
                  }
                >
                  {priorityOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </label>
            </div>

            <div className="scenario-status">
              <span>시나리오 일치</span>
              <strong>
                {selectedRegion} · {selectedCustomer} · {selectedPriority}
              </strong>
              <p>
                모든 전략 결과가 동일한 지역과 고객군을 기준으로 계산됩니다.
              </p>
            </div>

            {(competitionError || marketStrategyError) && (
              <p className="connection-error">
                통합 결과를 불러오려면 백엔드 서버를 실행해 주세요.
              </p>
            )}

            {competitionResult &&
              marketStrategyResult &&
              result &&
              pilotResult &&
              manufacturerResult &&
              deliveryResult && (
                <div className="summary-result-grid">
                  <article>
                    <span>시장 기회</span>
                    <strong>{marketStrategyResult.regionLabel}</strong>
                    <p>{marketStrategyResult.marketOpportunity}</p>
                  </article>
                  <article>
                    <span>제품 경쟁력</span>
                    <strong>{competitionResult.recommendedProduct}</strong>
                    <p>{competitionResult.reason}</p>
                  </article>
                  <article>
                    <span>수익성 · B2B ROI</span>
                    <strong>
                      {result.paybackPeriodMonths
                        ? `${result.paybackPeriodMonths}개월 회수`
                        : "회수기간 산정 불가"}
                    </strong>
                    <p>투자 판단: {result.recommendation}</p>
                  </article>
                  <article>
                    <span>파일럿 성과 검증</span>
                    <strong>
                      도입 효과{" "}
                      {formatPercent(
                        pilotResult.estimatedEffectPercentagePoint,
                      )}
                      p
                    </strong>
                    <p>확대 판단: {pilotResult.recommendation}</p>
                  </article>
                  <article>
                    <span>제조 가능성</span>
                    <strong>
                      대당 {formatCurrency(manufacturerResult.perUnitSavings)}{" "}
                      절감
                    </strong>
                    <p>
                      CAPEX 회수기간:{" "}
                      {manufacturerResult.paybackPeriodYears
                        ? `${manufacturerResult.paybackPeriodYears}년`
                        : "산정 불가"}
                    </p>
                  </article>
                  <article>
                    <span>설치 가능성</span>
                    <strong>
                      {deliveryResult.wallInstallPossible
                        ? "설치 공간 확보"
                        : "설치 공간 보완 필요"}
                    </strong>
                    <p>
                      권장 반입·조립 방식: {deliveryResult.recommendedMethod}
                    </p>
                  </article>
                  <article className="summary-action">
                    <span>영업 실행 방향</span>
                    <strong>“{marketStrategyResult.recommendedMessage}”</strong>
                    <p>
                      권장 채널:{" "}
                      {marketStrategyResult.recommendedChannels.join(" · ")}
                    </p>
                  </article>
                </div>
              )}

            {competitionResult &&
              marketStrategyResult &&
              result &&
              pilotResult &&
              manufacturerResult &&
              deliveryResult && (
                <section
                  className="ax-recommendation-card"
                  aria-label="최종 AX 권고"
                >
                  <div className="ax-recommendation-heading">
                    <div>
                      <p className="eyebrow">FINAL AX RECOMMENDATION</p>
                      <h3>최종 AX 권고</h3>
                      {finalRecommendation?.verdict && (
                        <span
                          className={`decision-badge ${finalRecommendation.verdict === "Go" ? "decision-go" : finalRecommendation.verdict === "보완 필요" ? "decision-blocked" : "decision-conditional"}`}
                        >
                          최종 판정 · {finalRecommendation.verdict}
                        </span>
                      )}
                    </div>
                    <div className="ax-recommendation-actions">
                      <div className="recommendation-option">
                        <button
                          type="button"
                          onClick={() => generateFinalRecommendation("codex")}
                          disabled={isGeneratingFinalRecommendation}
                        >
                          {isGeneratingFinalRecommendation
                            ? "작성 중…"
                            : "Codex로 종합 의견 만들기"}
                        </button>
                        <small>Codex 연결이 필요합니다.</small>
                      </div>
                      <div className="recommendation-option">
                        <button
                          type="button"
                          className="scenario-recommendation-button"
                          onClick={() =>
                            generateFinalRecommendation("scenario")
                          }
                          disabled={isGeneratingFinalRecommendation}
                        >
                          {isGeneratingFinalRecommendation
                            ? "작성 중…"
                            : "입력한 내용으로 바로 판단하기"}
                        </button>
                        <small>
                          Codex 연결 없이 6개 도구 결과를 바탕으로 작성합니다.
                        </small>
                      </div>
                    </div>
                  </div>
                  {isGeneratingFinalRecommendation ? (
                    <div className="recommendation-loading" role="status">
                      <i aria-hidden="true" />
                      <strong>권고를 생성중입니다…</strong>
                      <span>
                        현재 분석 결과를 종합하고 있습니다. 잠시만 기다려
                        주세요.
                      </span>
                    </div>
                  ) : finalRecommendation?.recommendation ? (
                    <>
                      <p className="ax-recommendation-summary">
                        {finalRecommendation.recommendation}
                      </p>
                      <ul>
                        <li>
                          <strong>먼저 해볼 일:</strong>{" "}
                          {finalRecommendation.nextAction}
                        </li>
                      </ul>
                      <div className="decision-reasons">
                        <strong>판정 근거</strong>
                        <ul>
                          {finalRecommendation.decisionReasons?.map(
                            (reason) => (
                              <li key={reason}>{reason}</li>
                            ),
                          )}
                        </ul>
                      </div>
                      <small>※ {finalRecommendation.statusMessage}</small>
                    </>
                  ) : (
                    <p className="ax-recommendation-summary">
                      현재 통합 분석 결과를 바탕으로 실제 Codex 권고문을 생성할
                      수 있습니다. 위 버튼을 눌러 시작하세요.
                    </p>
                  )}
                  <p className="verification-note final-data-disclaimer">
                    판정 시 각 도구에 입력한 값을 실제 데이터로 간주합니다.
                    따라서 입력한 수치와 조건이 모든 기준을 통과하면 Go 판정이
                    나올 수 있습니다. 실제 사업 적용 전에는 파일럿·견적·현장
                    결과와 대조해 주세요.
                  </p>
                  {finalRecommendationError && (
                    <p className="final-recommendation-error">
                      {finalRecommendationError}
                    </p>
                  )}
                </section>
              )}
          </section>
        </section>

        <section
          className={`department-group ${selectedModule === "manufacturer" ? "" : "department-hidden"}`}
        >
          <section
            className="manufacturer-card"
            aria-labelledby="manufacturer-title"
          >
            <div className="section-heading">
              <div>
                <p className="eyebrow"></p>
                <h2 id="manufacturer-title">100인치 OLED 사업성 시뮬레이터</h2>
                <p>
                  OMM 대비 FLiPP의 수율과 원가절감 가정을 반영해 BOM, 연간 절감
                  효과, CAPEX 회수기간을 분석합니다.
                </p>
              </div>
            </div>

            <div className="manufacturer-input-grid">
              {manufacturerFields.map((field) => (
                <label className="slider-field" key={field.key}>
                  <span>
                    {field.label}
                    <strong>
                      {formatManufacturerValue(
                        field,
                        manufacturerInputs[field.key],
                      )}
                    </strong>
                  </span>
                  <input
                    type="range"
                    min={field.min}
                    max={field.max}
                    step={field.step}
                    value={manufacturerInputs[field.key]}
                    onChange={(event) =>
                      updateManufacturerInput(field.key, event.target.value)
                    }
                  />
                </label>
              ))}
            </div>

            {manufacturerError && (
              <p className="connection-error">{manufacturerError}</p>
            )}

            {manufacturerResult && (
              <>
                <section
                  className="manufacturer-result-grid"
                  aria-label="제조사 사업성 분석 결과"
                >
                  <div>
                    <p>OMM 기준 BOM</p>
                    <strong>{formatCurrency(manufacturerResult.ommBom)}</strong>
                  </div>
                  <div>
                    <p>FLiPP 적용 BOM</p>
                    <strong>
                      {formatCurrency(manufacturerResult.fippBom)}
                    </strong>
                  </div>
                  <div>
                    <p>대당 원가 절감액</p>
                    <strong>
                      {formatCurrency(manufacturerResult.perUnitSavings)}
                    </strong>
                  </div>
                  <div>
                    <p>연간 원가 절감 효과</p>
                    <strong>
                      {formatCurrency(manufacturerResult.annualSavings)}
                    </strong>
                  </div>
                  <div>
                    <p>CAPEX 회수기간</p>
                    <strong>
                      {manufacturerResult.paybackPeriodYears
                        ? `${manufacturerResult.paybackPeriodYears}년`
                        : "산정 불가"}
                    </strong>
                  </div>
                </section>
                <section className="strategy-card">
                  <p>
                    투자 판단{" "}
                    <strong>{manufacturerResult.recommendation}</strong>
                  </p>
                  <h3>
                    FLiPP 적용 제조원가율:{" "}
                    {manufacturerResult.manufacturingCostRatioPercent}%
                  </h3>
                  <span>{manufacturerResult.reason}</span>
                </section>
              </>
            )}
          </section>
        </section>

        <section
          className={`department-group ${["roi", "pilot"].includes(selectedModule) ? "" : "department-hidden"}`}
        >
          <section
            className={`roi-card ${selectedModule === "roi" ? "" : "department-hidden"}`}
            aria-labelledby="roi-title"
          >
            <div className="roi-heading">
              <div>
                <h2 id="roi-title">B2B ROI Simulator</h2>
                <p>
                  PPT 12페이지 가정값을 기반으로 입력값을 바꾸며 사업주 투자
                  회수기간을 확인합니다.
                </p>
              </div>
            </div>

            <div className="input-list">
              {inputFields.map((field) => (
                <label className="slider-field" key={field.key}>
                  <span>
                    {field.label}
                    <strong>
                      {field.suffix === "%"
                        ? `${inputs[field.key]}%`
                        : formatCurrency(inputs[field.key])}
                    </strong>
                  </span>
                  <input
                    type="range"
                    min={field.min}
                    max={field.max}
                    step={field.step}
                    value={inputs[field.key]}
                    onChange={(event) =>
                      updateInput(field.key, event.target.value)
                    }
                  />
                </label>
              ))}
            </div>

            {error && <p className="connection-error">{error}</p>}

            {result && (
              <>
                <section className="result-grid" aria-label="ROI 계산 결과">
                  <div>
                    <p>
                      월 추가 공헌이익 <span>(운영비 차감)</span>
                    </p>
                    <strong>
                      {formatCurrency(result.monthlyContributionProfit)}
                    </strong>
                  </div>
                  <div>
                    <p>예상 투자 회수기간</p>
                    <strong>
                      {result.paybackPeriodMonths
                        ? `${result.paybackPeriodMonths}개월`
                        : "산정 불가"}
                    </strong>
                  </div>
                  <div className="recommendation">
                    <p>투자 판단</p>
                    <strong>{result.recommendation}</strong>
                  </div>
                </section>

                <section className="formula-card">
                  <h3>계산식</h3>
                  <p>
                    월 추가 공헌이익 = 월 추가 매출 × 공헌이익률 − 추가 운영비
                  </p>
                  <p>투자 회수기간 = 총 투자비 ÷ 월 추가 공헌이익</p>
                  <p className="reason">{result.reason}</p>
                </section>
              </>
            )}
          </section>

          <section
            className={`pilot-card ${selectedModule === "pilot" ? "" : "department-hidden"}`}
            aria-labelledby="pilot-title"
          >
            <div className="section-heading">
              <div>
                <p className="eyebrow"></p>
                <h2 id="pilot-title">파일럿 매장 성과 검증 Tool</h2>
                <p>
                  시범 매장과 비교 매장의 설치 전후 매출 변화율 차이로 OLED 도입
                  효과를 검증합니다.
                </p>
              </div>
            </div>

            <div className="store-counts">
              <label>
                시범 매장 수
                <input
                  type="number"
                  min="1"
                  max="10"
                  value={pilotInputs.pilotStoreCount}
                  onChange={(event) =>
                    updatePilotInput("pilotStoreCount", event.target.value)
                  }
                />
              </label>
              <label>
                비교 매장 수
                <input
                  type="number"
                  min="1"
                  max="10"
                  value={pilotInputs.controlStoreCount}
                  onChange={(event) =>
                    updatePilotInput("controlStoreCount", event.target.value)
                  }
                />
              </label>
            </div>

            <div className="pilot-input-grid">
              {pilotSalesFields.map((field) => (
                <label className="slider-field" key={field.key}>
                  <span>
                    {field.label}
                    <strong>{formatCurrency(pilotInputs[field.key])}</strong>
                  </span>
                  <input
                    type="range"
                    min={field.min}
                    max={field.max}
                    step={field.step}
                    value={pilotInputs[field.key]}
                    onChange={(event) =>
                      updatePilotInput(field.key, event.target.value)
                    }
                  />
                </label>
              ))}
            </div>

            <p className="verification-note">
              판정에서는 입력한 시범·비교 매출을 실제 측정값으로 간주해 도입
              효과를 계산합니다. 실제 확대 결정 전에는 매장 데이터를 대조해
              주세요.
            </p>

            {pilotError && <p className="connection-error">{pilotError}</p>}

            {pilotResult && (
              <>
                <section
                  className="pilot-result-grid"
                  aria-label="파일럿 성과 결과"
                >
                  <div>
                    <p>시범 매장 변화율</p>
                    <strong>
                      {formatPercent(pilotResult.pilotChangePercent)}
                    </strong>
                  </div>
                  <div>
                    <p>비교 매장 변화율</p>
                    <strong>
                      {formatPercent(pilotResult.controlChangePercent)}
                    </strong>
                  </div>
                  <div className="effect-result">
                    <p>OLED 도입 추정 효과</p>
                    <strong>
                      {formatPercent(
                        pilotResult.estimatedEffectPercentagePoint,
                      )}
                      p
                    </strong>
                  </div>
                  <div className="recommendation">
                    <p>확대 판단</p>
                    <strong>{pilotResult.recommendation}</strong>
                  </div>
                </section>
                <section className="formula-card">
                  <h3>검증식</h3>
                  <p>
                    OLED 도입 추정 효과 = 시범 매장 변화율 − 비교 매장 변화율
                  </p>
                  <p className="reason">{pilotResult.reason}</p>
                </section>
              </>
            )}
          </section>
        </section>

        <section
          className={`department-group ${selectedModule === "competition" ? "" : "department-hidden"}`}
        >
          <section
            className="competition-card"
            aria-labelledby="competition-title"
          >
            <div className="section-heading">
              <div>
                <p className="eyebrow"></p>
                <h2 id="competition-title">경쟁력 비교 Tool</h2>
                <p>
                  OMM OLED, Mini-LED, FLiPP OLED를 고객군별 우선순위로
                  비교합니다.
                </p>
              </div>
            </div>

            <div className="shared-scenario-note">
              <span>통합 대시보드 공통 시나리오</span>
              <strong>
                {selectedRegion} · {selectedCustomer} · {selectedPriority}
              </strong>
              <button
                type="button"
                onClick={openScenarioEditor}
              >
                시나리오 변경
              </button>
            </div>

            {competitionError && (
              <p className="connection-error">{competitionError}</p>
            )}

            {competitionResult && (
              <>
                <div className="score-list">
                  {competitionResult.products.map((product, index) => (
                    <article className="score-row" key={product.product}>
                      <span className="rank">{index + 1}</span>
                      <div className="score-label">
                        <strong>{product.product}</strong>
                        <span>{product.strengths.join(" · ")}</span>
                      </div>
                      <div className="score-bar">
                        <i style={{ width: `${product.score * 20}%` }} />
                      </div>
                      <strong className="score-value">
                        {product.score}
                        <small>/ 5.0</small>
                      </strong>
                    </article>
                  ))}
                </div>
                <section className="strategy-card">
                  <p>
                    추천 제품{" "}
                    <strong>{competitionResult.recommendedProduct}</strong>
                  </p>
                  <h3>“{competitionResult.salesMessage}”</h3>
                  <span>{competitionResult.reason}</span>
                </section>
              </>
            )}
          </section>
        </section>

        <section
          className={`department-group ${selectedModule === "marketStrategy" ? "" : "department-hidden"}`}
        >
          <section
            className="market-strategy-card"
            aria-labelledby="market-strategy-title"
          >
            <div className="section-heading">
              <div>
                <p className="eyebrow"></p>
                <h2 id="market-strategy-title">시장·고객 전략 추천 Tool</h2>
                <p>
                  지역과 핵심 고객군을 선택하면 FLiPP OLED의 고객가치, 영업
                  메시지와 권장 채널을 제안합니다.
                </p>
              </div>
            </div>

            <div className="shared-scenario-note">
              <span>통합 대시보드 공통 시나리오</span>
              <strong>
                {selectedRegion} · {selectedCustomer}
              </strong>
              <button
                type="button"
                onClick={openScenarioEditor}
              >
                시나리오 변경
              </button>
            </div>

            {marketStrategyError && (
              <p className="connection-error">{marketStrategyError}</p>
            )}

            {marketStrategyResult && (
              <>
                <section className="market-opportunity-card">
                  <p>시장 기회 · {marketStrategyResult.regionLabel}</p>
                  <h3>{marketStrategyResult.marketOpportunity}</h3>
                </section>
                <section className="market-result-grid">
                  <article>
                    <p>핵심 고객가치</p>
                    <ul>
                      {marketStrategyResult.coreCustomerValues.map((value) => (
                        <li key={value}>{value}</li>
                      ))}
                    </ul>
                  </article>
                  <article>
                    <p>권장 영업 채널</p>
                    <ul>
                      {marketStrategyResult.recommendedChannels.map(
                        (channel) => (
                          <li key={channel}>{channel}</li>
                        ),
                      )}
                    </ul>
                  </article>
                </section>
                <section className="strategy-card">
                  <p>
                    추천 메시지 ·{" "}
                    <strong>{marketStrategyResult.customerLabel}</strong>
                  </p>
                  <h3>“{marketStrategyResult.recommendedMessage}”</h3>
                  <span>
                    뉴스 검색어: {marketStrategyResult.newsSearchQuery}
                  </span>
                </section>
                <section
                  className="market-news-section"
                  aria-label="시장 근거 기사"
                >
                  <div className="market-news-heading">
                    <div>
                      <p className="eyebrow">MARKET EVIDENCE</p>
                      <h3>시장 근거 기사</h3>
                    </div>
                    <span>네이버 뉴스 · 최신순</span>
                  </div>
                  {marketStrategyResult.newsArticles?.length > 0 ? (
                    <div className="market-news-list">
                      {marketStrategyResult.newsArticles.map((article) => (
                        <a
                          className="market-news-card"
                          href={article.originalLink}
                          target="_blank"
                          rel="noreferrer"
                          key={article.originalLink}
                        >
                          <span>
                            {article.source} · {article.publishedAt}
                          </span>
                          <strong>{article.title}</strong>
                          <p>{article.summary}</p>
                          <em>원문 보기 ↗</em>
                        </a>
                      ))}
                    </div>
                  ) : (
                    <p className="news-status-message">
                      {marketStrategyResult.newsStatusMessage}
                    </p>
                  )}
                </section>
              </>
            )}
          </section>
        </section>

        <section
          className={`department-group sales-return ${selectedModule === "delivery" ? "" : "department-hidden"}`}
        >
          <section className="delivery-card" aria-labelledby="delivery-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow"></p>
                <h2 id="delivery-title">100인치 OLED 반입·조립 시뮬레이터</h2>
                <p>
                  반입 경로와 설치 벽면 규격을 입력하면 완제품·2분할·4분할
                  방식의 가능 여부를 비교합니다.
                </p>
              </div>
            </div>

            <div className="delivery-input-grid">
              {deliveryFields.map((field) => (
                <label className="slider-field" key={field.key}>
                  <span>
                    {field.label}
                    <strong>
                      {deliveryInputs[field.key].toLocaleString("ko-KR")}mm
                    </strong>
                  </span>
                  <input
                    type="range"
                    min={field.min}
                    max={field.max}
                    step={field.step}
                    value={deliveryInputs[field.key]}
                    onChange={(event) =>
                      updateDeliveryInput(field.key, event.target.value)
                    }
                  />
                </label>
              ))}
            </div>

            {deliveryError && (
              <p className="connection-error">{deliveryError}</p>
            )}

            {deliveryResult && (
              <>
                <div className="delivery-summary">
                  <div>
                    <span>100인치 제품 기준</span>
                    <strong>
                      {deliveryResult.productWidth.toLocaleString("ko-KR")} ×{" "}
                      {deliveryResult.productHeight.toLocaleString("ko-KR")}mm
                    </strong>
                  </div>
                  <div>
                    <span>설치 벽면</span>
                    <strong
                      className={
                        deliveryResult.wallInstallPossible ? "pass" : "fail"
                      }
                    >
                      {deliveryResult.wallInstallPossible
                        ? "설치 가능"
                        : "설치 공간 부족"}
                    </strong>
                  </div>
                </div>

                <div className="delivery-method-list">
                  {deliveryResult.methods.map((method) => (
                    <article
                      className={`delivery-method ${method.method === deliveryResult.recommendedMethod ? "selected" : ""}`}
                      key={method.method}
                    >
                      <div
                        className={`module-visual module-${method.moduleCount}`}
                        aria-label={`${method.moduleCount}개 모듈 구성`}
                      >
                        {Array.from(
                          { length: method.moduleCount },
                          (_, index) => (
                            <i key={index}>{index + 1}</i>
                          ),
                        )}
                      </div>
                      <div className="method-details">
                        <h3>
                          {method.method}{" "}
                          {method.method ===
                            deliveryResult.recommendedMethod && (
                            <span>추천</span>
                          )}
                        </h3>
                        <p>
                          모듈당 포장 규격: {method.packageWidth} ×{" "}
                          {method.packageHeight}mm
                        </p>
                        <small>{method.note}</small>
                      </div>
                      <div className="route-checks">
                        <span className={method.doorPass ? "pass" : "fail"}>
                          현관 {method.doorPass ? "가능" : "불가"}
                        </span>
                        <span
                          className={method.elevatorDoorPass ? "pass" : "fail"}
                        >
                          엘리베이터 문{" "}
                          {method.elevatorDoorPass ? "가능" : "불가"}
                        </span>
                        <span
                          className={method.elevatorCabinPass ? "pass" : "fail"}
                        >
                          내부 적재 {method.elevatorCabinPass ? "가능" : "불가"}
                        </span>
                      </div>
                    </article>
                  ))}
                </div>

                <section className="strategy-card">
                  <p>
                    최종 추천{" "}
                    <strong>{deliveryResult.recommendedMethod}</strong>
                  </p>
                  <h3>{deliveryResult.recommendation}</h3>
                  <span>
                    본 결과는 포장 규격 기반의 1차 시뮬레이션입니다. 실제 설치
                    전에는 복도 회전 반경, 하중 및 현장 실측이 필요합니다.
                  </span>
                </section>
              </>
            )}
          </section>
        </section>

        <footer>
          본 결과는 입력한 가정값을 기준으로 한 단순 투자 회수기간 계산입니다.
        </footer>
      </div>
    </main>
  );
}

export default App;
