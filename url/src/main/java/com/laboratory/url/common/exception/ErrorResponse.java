package com.laboratory.url.common.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
	LocalDateTime timestamp,
	String code,
	String message
) {
	public static ErrorResponse of(ErrorCode errorCode, String message) {
		return new ErrorResponse(LocalDateTime.now(), errorCode.getCode(), message);
	}
}
