package com.laboratory.logback.common.slack;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Slack 에러 알림 한 건을 표현하는 모델. (참고 이미지의 필드 구성과 1:1 매핑)
 * severity 값에 따라 왼쪽 컬러 바 색상이 결정된다.
 */
@Builder
public record SlackErrorMessage(
        String type,              // 유형
        String code,              // 코드
        Severity severity,        // 심각도
        String environment,       // 환경
        String requestId,         // Request ID (= traceId)
        String userId,            // User ID
        String requestUri,        // Request URI
        LocalDateTime occurredAt, // 발생 시간
        String server,            // 서버
        String message            // 메시지
) {
    public enum Severity {
        INFO("#2EB67D"),   // green
        WARN("#ECB22E"),   // amber
        ERROR("#E01E5A");  // red

        private final String color;

        Severity(String color) {
            this.color = color;
        }

        public String color() {
            return color;
        }
    }
}
