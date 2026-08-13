package com.laboratory.url.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PayloadException.class)
	public ResponseEntity<ErrorResponse> handlePayload(PayloadException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.warn("[PayloadException] code={}, message={}", errorCode.getCode(), e.getMessage());
		return ResponseEntity.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode, e.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
		log.error("[UnexpectedException] message={}", e.getMessage(), e);
		return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
			.body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getMessage()));
	}
}
