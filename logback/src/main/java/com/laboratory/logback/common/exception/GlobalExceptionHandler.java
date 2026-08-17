package com.laboratory.logback.common.exception;

import com.laboratory.logback.common.logging.TraceIdFilter;
import com.laboratory.logback.common.slack.SlackErrorMessage;
import com.laboratory.logback.common.slack.SlackNotifier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 전역 예외 처리.
 * - BusinessException : 의도된 비즈니스 오류 → WARN 로깅 + Slack(WARN, 앰버) 알림, 정의된 상태코드 응답
 * - 검증 실패          : 필드별 사유를 담아 400 응답
 * - 그 외 Exception    : 예상치 못한 오류 → ERROR 로깅 + Slack(ERROR, 레드) 알림, 500 응답
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final SlackNotifier slackNotifier;
    private final Environment environment;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        String traceId = traceId();
        log.warn("[BusinessException] code={}, message={}", errorCode.getCode(), e.getMessage());
        slackNotifier.send(buildSlackMessage(
                "비즈니스 예외", errorCode.getCode(), SlackErrorMessage.Severity.WARN,
                request, traceId, e.getMessage()));
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, e.getMessage(), traceId, List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<ErrorResponse.FieldErrorDetail> details = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldErrorDetail(fe.getField(), fe.getDefaultMessage()))
                .toList();
        log.warn("[ValidationException] {}", details);
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, ErrorCode.INVALID_INPUT.getMessage(),
                        traceId(), details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e, HttpServletRequest request) {
        String traceId = traceId();
        log.error("[UnexpectedException] traceId={}, message={}", traceId, e.getMessage(), e);
        slackNotifier.send(buildSlackMessage(
                "서버 내부 오류", e.getClass().getSimpleName(), SlackErrorMessage.Severity.ERROR,
                request, traceId, e.toString()));
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, traceId));
    }

    private SlackErrorMessage buildSlackMessage(String type, String code, SlackErrorMessage.Severity severity,
                                                HttpServletRequest request, String traceId, String message) {
        return SlackErrorMessage.builder()
                .type(type)
                .code(code)
                .severity(severity)
                .environment(activeProfile())
                .requestId(traceId)
                .userId(header(request, "X-User-Id"))
                .requestUri(request.getMethod() + " " + request.getRequestURI())
                .occurredAt(LocalDateTime.now())
                .server(serverIp())
                .message(message)
                .build();
    }

    private String traceId() {
        return MDC.get(TraceIdFilter.TRACE_ID);
    }

    private String activeProfile() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length == 0 ? "default" : String.join(",", profiles);
    }

    private String serverIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "-";
        }
    }

    private String header(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        return (value == null || value.isBlank()) ? "-" : value;
    }
}
