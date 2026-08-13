package com.laboratory.url.common.exception;

import lombok.Getter;

@Getter
public class PayloadException extends RuntimeException {

	private final ErrorCode errorCode;

	public PayloadException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public PayloadException(ErrorCode errorCode, String detail) {
		super(errorCode.getMessage() + " (" + detail + ")");
		this.errorCode = errorCode;
	}

	public PayloadException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage() + " (" + cause.getClass().getSimpleName() + ": " + cause.getMessage() + ")", cause);
		this.errorCode = errorCode;
	}
}
