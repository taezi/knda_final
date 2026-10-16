package com.knda.ax.service;

import com.knda.ax.config.CodexCliProperties;
import com.knda.ax.dto.FinalAxRecommendationRequest;
import com.knda.ax.dto.FinalAxRecommendationResponse;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FinalAxRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(FinalAxRecommendationService.class);
    private static final int MAX_DIAGNOSTIC_LENGTH = 1200;

    private static final String OUTPUT_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "verdict": { "type": "string" },
                "recommendation": { "type": "string" },
                "nextAction": { "type": "string" }
              },
              "required": ["verdict", "recommendation", "nextAction"],
              "additionalProperties": false
            }
            """;

    private final CodexCliProperties codexCliProperties;
    private final FinalAxDecisionPolicy decisionPolicy;
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    public FinalAxRecommendationService(
            CodexCliProperties codexCliProperties,
            FinalAxDecisionPolicy decisionPolicy
    ) {
        this.codexCliProperties = codexCliProperties;
        this.decisionPolicy = decisionPolicy;
    }

    public FinalAxRecommendationResponse recommend(FinalAxRecommendationRequest request) {
        FinalAxDecisionPolicy.Decision decision = decisionPolicy.evaluate(request);
        if (!codexCliProperties.enabled()) {
            throw unavailable("Codex CLI가 활성화되지 않았습니다.");
        }

        Path runDirectory = null;
        try {
            runDirectory = Files.createTempDirectory("ax-codex-recommendation-");
            Path schemaPath = runDirectory.resolve("response-schema.json");
            Path responsePath = runDirectory.resolve("response.json");
            Path errorPath = runDirectory.resolve("codex-error.log");
            Files.writeString(schemaPath, OUTPUT_SCHEMA, StandardCharsets.UTF_8);

            Process process = createProcess(schemaPath, responsePath, errorPath);
            writePrompt(process, buildPrompt(request, decision));

            boolean completed = process.waitFor(codexCliProperties.timeoutSecondsOrDefault(), TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                throw unavailable("Codex 응답 시간이 초과되었습니다.");
            }

            if (process.exitValue() != 0 || Files.notExists(responsePath)) {
                String output = readProcessOutput(errorPath);
                log.error("[Codex CLI] exitCode={}, responseFileCreated={}, outputTail={}",
                        process.exitValue(), Files.exists(responsePath), diagnosticTail(output));
                throw unavailable("Codex 실행에 실패했습니다 (종료 코드 " + process.exitValue()
                        + "). IntelliJ 백엔드 콘솔의 [Codex CLI] 로그를 확인해 주세요. " + failureReason(output));
            }

            JsonNode response = jsonMapper.readTree(Files.readString(responsePath, StandardCharsets.UTF_8));
            requiredText(response, "verdict");
            String recommendation = requiredText(response, "recommendation");
            String nextAction = requiredText(response, "nextAction");

            return new FinalAxRecommendationResponse(
                    decision.verdict(),
                    recommendation,
                    nextAction,
                    true,
                    "판정은 프로젝트 기준으로 계산하고, Codex는 설명문을 작성했습니다.",
                    decision.reasons()
            );
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw unavailable("Codex 권고 생성이 중단되었습니다.");
        } catch (IOException exception) {
            log.error("[Codex CLI] 프로세스 실행 또는 입출력 실패: {}", exception.toString());
            throw unavailable("Codex CLI를 시작하지 못했습니다.");
        } catch (RuntimeException exception) {
            log.error("[Codex CLI] 응답 처리 실패: {}", exception.toString());
            throw unavailable("Codex 응답 형식을 확인하지 못했습니다.");
        } finally {
            deleteTemporaryFiles(runDirectory);
        }
    }

    private Process createProcess(Path schemaPath, Path responsePath, Path errorPath) throws IOException {
        String workingDirectory = codexCliProperties.workingDirectory();
        if (workingDirectory == null || workingDirectory.isBlank()) {
            workingDirectory = System.getProperty("user.dir");
        }

        String codexCommand = codexCliProperties.commandOrDefault();
        List<String> command = new ArrayList<>();
        if (codexCommand.endsWith(".cmd") || codexCommand.endsWith(".bat")) {
            command.add("cmd.exe");
            command.add("/c");
        }

        command.add(codexCommand);
        command.addAll(List.of(
                "exec",
                "--ephemeral",
                "--skip-git-repo-check",
                "--sandbox", "read-only",
                "-C", workingDirectory,
                "--output-schema", schemaPath.toString(),
                "--output-last-message", responsePath.toString(),
                "-"
        ));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        addCodexHomeEnvironment(processBuilder);
        addNodeDirectoryToPath(processBuilder);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(errorPath.toFile());
        return processBuilder.start();
    }

    private void addCodexHomeEnvironment(ProcessBuilder processBuilder) {
        var environment = processBuilder.environment();
        String userProfile = environment.get("USERPROFILE");
        if (userProfile == null || userProfile.isBlank()) {
            userProfile = System.getProperty("user.home");
        }
        if (userProfile == null || userProfile.isBlank()) return;

        environment.putIfAbsent("HOME", userProfile);
        environment.putIfAbsent("CODEX_HOME", Path.of(userProfile, ".codex").toString());
    }

    private void addNodeDirectoryToPath(ProcessBuilder processBuilder) {
        String nodeDirectory = codexCliProperties.nodeDirectory();
        if (nodeDirectory == null || nodeDirectory.isBlank()) {
            return;
        }

        var environment = processBuilder.environment();
        String currentPath = environment.getOrDefault("Path", environment.getOrDefault("PATH", ""));
        environment.put("Path", nodeDirectory + File.pathSeparator + currentPath);
    }

    private void writePrompt(Process process, String prompt) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write(prompt);
        }
    }

    private String buildPrompt(
            FinalAxRecommendationRequest request,
            FinalAxDecisionPolicy.Decision decision
    ) {
        return """
                당신은 100인치 FLiPP OLED AX 프로젝트의 최종 의사결정 권고 작성자입니다.
                도구를 사용하지 마세요. 아래에 제공된 분석 결과만 근거로 한국어 권고문을 작성하세요.
                외부 사실이나 수치를 새로 만들지 마세요. 입력값은 시나리오의 분석 결과로 다루고, 검증되지 않은 시장 사실처럼 단정하지 마세요.
                최종 판정은 반드시 다음 값 그대로 사용하세요: %s
                판정 근거: %s
                문체는 팀장이 팀원에게 설명하듯 자연스럽고 부드러운 한국어로 작성하세요.
                딱딱한 보고서 말투, 번역투, 불필요한 영어·전문용어, 같은 표현의 반복은 피하세요.
                recommendation은 2~3문장으로 제품과 시나리오의 적합성 및 주요 고려사항을 설명하세요.
                확정적이거나 재촉하는 표현 대신 근거에 맞춰 가능성과 확인할 점을 차분하게 표현하세요.
                nextAction은 실제로 시작할 수 있는 첫 행동을 1문장으로 제안하고, 실행을 명령하거나 근거 없이 '즉시'라고 재촉하지 마세요.
                nextAction은 가능하면 확인할 대상이나 기준을 구체적으로 포함하되, 제공되지 않은 계획·수치·사실은 만들지 마세요.

                [통합 분석 결과]
                - 공략 지역: %s
                - 핵심 고객군: %s
                - 구매 우선순위: %s
                - 제품 경쟁력 추천: %s
                - 시장 기회: %s
                - B2B ROI 회수기간: %s
                - B2B 월 추가 공헌이익: %.0f원
                - ROI 판단: %s
                - 파일럿 도입 효과: %.1f%%p (%s)
                - 고객·시장 적합성 검증: %s
                - 파일럿 판단: %s
                - FLiPP 대당 원가 절감액: %.0f원
                - CAPEX 회수기간: %s
                - 설치 공간 확보: %s
                - 반입 경로 시뮬레이션: %s
                - 재무 데이터 실측 확인: %s
                - 설치 현장 실측 확인: %s
                - 권장 반입·조립 방식: %s
                """.formatted(
                decision.verdict(),
                String.join("; ", decision.reasons()),
                request.regionLabel(),
                request.customerLabel(),
                request.priorityLabel(),
                request.recommendedProduct(),
                request.marketOpportunity(),
                request.roiPaybackPeriodMonths() == null ? "산정 불가" : request.roiPaybackPeriodMonths() + "개월",
                request.monthlyContributionProfit(),
                request.roiRecommendation(),
                request.pilotEffectPercentagePoint(),
                request.pilotDataVerified() ? "실측" : "가정",
                request.marketFitVerified() ? "확인됨" : "미확인",
                request.pilotRecommendation(),
                request.perUnitSavings(),
                request.capexPaybackPeriodYears() == null ? "산정 불가" : String.format("%.1f년", request.capexPaybackPeriodYears()),
                request.wallInstallPossible() ? "확보" : "미확보",
                request.deliveryRoutePossible() ? "가능" : "불가",
                request.financialDataVerified() ? "확인됨" : "미확인",
                request.deliverySiteVerified() ? "확인됨" : "미확인",
                request.recommendedDeliveryMethod()
        );
    }

    private String requiredText(JsonNode response, String fieldName) {
        String value = response.path(fieldName).asText();
        if (value.isBlank()) {
            throw new IllegalArgumentException("Codex 응답에 " + fieldName + " 값이 없습니다.");
        }
        return value;
    }

    private String failureReason(String output) {
        String lowerOutput = output.toLowerCase();
        if (output.contains("Could not find home directory")) {
            return "사용자 홈 경로를 찾지 못했습니다.";
        }
        if (output.contains("not recognized") || lowerOutput.contains("command not found")
                || lowerOutput.contains("not found")) {
            return "Codex CLI 실행 파일 또는 Node.js 경로를 찾지 못했습니다.";
        }
        if (lowerOutput.contains("login") || lowerOutput.contains("sign in")
                || lowerOutput.contains("auth")) {
            return "Codex CLI 로그인이 필요합니다.";
        }
        if (lowerOutput.contains("rate limit") || lowerOutput.contains("usage limit")) {
            return "Codex 사용량 한도에 도달했습니다.";
        }
        return "실행 환경을 확인해 주세요.";
    }

    private String readProcessOutput(Path errorPath) {
        try {
            byte[] bytes = Files.readAllBytes(errorPath);
            if (bytes.length >= 2 && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xfe) {
                return new String(bytes, 2, bytes.length - 2, StandardCharsets.UTF_16LE);
            }
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (IOException exception) {
            return "Codex 로그 파일을 읽지 못했습니다: " + exception.getMessage();
        }
    }

    private String diagnosticTail(String output) {
        if (output == null || output.isBlank()) {
            return "<출력 없음>";
        }
        String sanitized = output
                .replaceAll("(?i)(api[-_ ]?key|secret|token|authorization)(\\s*[:=]\\s*)[^\\s,;]+", "$1$2<REDACTED>")
                .replaceAll("(?i)(authorization\\s*[:=]\\s*)(?:bearer\\s+)?[^\\s,;]+", "$1<REDACTED>")
                .replaceAll("\\u001B\\[[;\\d]*m", "")
                .strip();
        if (sanitized.length() > MAX_DIAGNOSTIC_LENGTH) {
            sanitized = sanitized.substring(sanitized.length() - MAX_DIAGNOSTIC_LENGTH);
        }
        return sanitized.replaceAll("[\\r\\n]+", " | ");
    }

    private ResponseStatusException unavailable(String message) {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, message);
    }

    private void deleteTemporaryFiles(Path runDirectory) {
        if (runDirectory == null) {
            return;
        }

        try {
            Files.deleteIfExists(runDirectory.resolve("response-schema.json"));
            Files.deleteIfExists(runDirectory.resolve("response.json"));
            Files.deleteIfExists(runDirectory.resolve("codex-error.log"));
            Files.deleteIfExists(runDirectory);
        } catch (IOException ignored) {
            // Temporary files are not required after the response is returned.
        }
    }
}
