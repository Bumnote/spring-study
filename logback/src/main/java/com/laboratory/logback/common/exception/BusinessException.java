package com.laboratory.logback.common.exception;

import lombok.Getter;

/**
 * 비즈니스 규칙 위반을 표현하는 예외. ErrorCode를 통해 응답 상태/코드/메시지를 일관되게 관리한다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }
}
