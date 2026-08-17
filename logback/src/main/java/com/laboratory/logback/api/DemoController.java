package com.laboratory.logback.api;

import com.laboratory.logback.common.exception.BusinessException;
import com.laboratory.logback.common.exception.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세 가지 목표(JSON 로깅 / 전역 예외 / Slack 알림)를 직접 눌러볼 수 있는 데모 엔드포인트.
 */
@Slf4j
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    /** 정상 로그 → JSON(traceId 포함) 출력 확인 */
    @GetMapping("/hello")
    public String hello() {
        log.info("hello 호출됨");
        return "hello";
    }

    /** 비즈니스 예외 → 정의된 상태코드/코드로 응답 */
    @GetMapping("/business-error")
    public String businessError() {
        log.info("business-error 호출됨");
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
    }

    /** 예상치 못한 예외 → ERROR 로깅 + Slack 알림 + 500 응답 */
    @GetMapping("/unexpected-error")
    public String unexpectedError() {
        log.info("unexpected-error 호출됨");
        throw new IllegalStateException("예상치 못한 오류 예시");
    }

    /** @Valid 검증 실패 → 필드별 사유를 담아 400 응답 */
    @PostMapping("/echo")
    public EchoResponse echo(@Valid @RequestBody EchoRequest request) {
        log.info("echo 호출됨: {}", request.message());
        return new EchoResponse(request.message());
    }

    public record EchoRequest(@NotBlank(message = "message는 필수입니다.") String message) {
    }

    public record EchoResponse(String message) {
    }
}
