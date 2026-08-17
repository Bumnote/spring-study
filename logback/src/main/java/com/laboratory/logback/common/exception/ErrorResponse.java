package com.laboratory.logback.common.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API 공통 에러 응답 포맷. traceId를 함께 내려주어 로그(구조적 JSON)와 응답을 연결할 수 있게 한다.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        String code,
        String message,
        String traceId,
        List<FieldErrorDetail> errors
) {
    public record FieldErrorDetail(String field, String reason) {
    }

    public static ErrorResponse of(ErrorCode errorCode, String traceId) {
        return of(errorCode, errorCode.getMessage(), traceId, List.of());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, String traceId,
                                   List<FieldErrorDetail> errors) {
        return new ErrorResponse(LocalDateTime.now(), errorCode.getCode(), message, traceId, errors);
    }
}
