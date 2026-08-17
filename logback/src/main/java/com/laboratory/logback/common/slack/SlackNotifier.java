package com.laboratory.logback.common.slack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 예외 발생 시 Slack으로 알림을 전송한다.
 * - @Async 로 요청 스레드를 블로킹하지 않는다.
 * - attachment(color) + Block Kit(fields) 조합으로 왼쪽 컬러 바 + 2열 카드 형태를 만든다.
 * - webhookUrl 미설정/비활성 시에는 전송을 건너뛰고 로그만 남긴다.
 */
@Slf4j
@Component
public class SlackNotifier {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SlackProperties properties;
    private final RestClient restClient;

    public SlackNotifier(SlackProperties properties) {
        this.properties = properties;
        // Slack 지연/장애가 알림 스레드를 오래 붙잡지 않도록 connect/read 타임아웃 지정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @Async
    public void send(SlackErrorMessage m) {
        if (!properties.enabled() || !StringUtils.hasText(properties.webhookUrl())) {
            log.info("[Slack] 비활성 상태 - 전송 생략 (type={}, requestId={})", m.type(), m.requestId());
            return;
        }
        try {
            restClient.post()
                    .uri(properties.webhookUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildPayload(m))
                    .retrieve()
                    .toBodilessEntity();
            log.debug("[Slack] 전송 완료 (requestId={})", m.requestId());
        } catch (Exception ex) {
            // 알림 실패가 원 요청 처리에 영향을 주지 않도록 삼킨다.
            log.warn("[Slack] 전송 실패 (requestId={}): {}", m.requestId(), ex.getMessage());
        }
    }

    /**
     * 참고 이미지와 동일한 레이아웃의 Slack 페이로드를 조립한다.
     * 2열 필드를 하나의 section(fields)로 최대한 합쳐 블록 간 여백을 줄인다
     * → 메시지 높이가 낮아져 Slack의 "간략히 보기(자동 접힘)"에 덜 걸린다.
     */
    private Map<String, Object> buildPayload(SlackErrorMessage m) {
        List<Map<String, Object>> blocks = List.of(
                // 유형/코드/심각도/환경/Request ID/User ID 6개 필드를 한 섹션으로 (3행 2열)
                fieldsSection(
                        "유형", m.type(),
                        "코드", m.code(),
                        "심각도", severityText(m.severity()),
                        "환경", m.environment(),
                        "Request ID", m.requestId(),
                        "User ID", m.userId()),
                fullWidth("Request URI", m.requestUri()),
                fieldsSection(
                        "발생 시간", formatTime(m.occurredAt()),
                        "서버", m.server()),
                codeBlock("메시지", m.message())
        );

        Map<String, Object> attachment = Map.of(
                "color", m.severity() == null ? SlackErrorMessage.Severity.INFO.color() : m.severity().color(),
                // 알림/미리보기용 fallback 텍스트
                "fallback", severityText(m.severity()) + " " + nullToDash(m.type()) + " / " + nullToDash(m.code()),
                "blocks", blocks
        );
        return Map.of("attachments", List.of(attachment));
    }

    /** 라벨/값 쌍들을 하나의 section(fields)로 합친다. Slack은 fields를 2열로 렌더링(최대 10개). */
    private Map<String, Object> fieldsSection(String... labelValuePairs) {
        List<Map<String, Object>> fields = new ArrayList<>();
        for (int i = 0; i + 1 < labelValuePairs.length; i += 2) {
            fields.add(mrkdwn(field(labelValuePairs[i], labelValuePairs[i + 1])));
        }
        return Map.of("type", "section", "fields", fields);
    }

    /** 값이 길어 한 줄을 다 쓰는 필드 (예: Request URI) */
    private Map<String, Object> fullWidth(String label, String value) {
        return Map.of("type", "section", "text", mrkdwn(field(label, value)));
    }

    /** 메시지를 코드블록으로 강조 */
    private Map<String, Object> codeBlock(String label, String value) {
        return Map.of("type", "section", "text", mrkdwn("*" + label + "*\n```" + nullToDash(value) + "```"));
    }

    private Map<String, Object> mrkdwn(String text) {
        return Map.of("type", "mrkdwn", "text", text);
    }

    private String field(String label, String value) {
        return "*" + label + "*\n" + nullToDash(value);
    }

    private String severityText(SlackErrorMessage.Severity severity) {
        return severity == null ? "-" : severity.name();
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? "-" : time.format(TIME_FORMAT);
    }

    private String nullToDash(String value) {
        return StringUtils.hasText(value) ? value : "-";
    }
}
