package com.laboratory.url.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	EMPTY_PAYLOAD(HttpStatus.BAD_REQUEST, "P001", "요청 본문이 비어 있습니다."),
	UNRESOLVABLE_PAYLOAD(HttpStatus.BAD_REQUEST, "P002", "페이로드를 JSON으로 해석할 수 없습니다."),
	PAYLOAD_DECRYPT_FAILED(HttpStatus.BAD_REQUEST, "P003", "AES 복호화에 실패했습니다."),
	PAYLOAD_ENCRYPT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "P004", "AES 암호화에 실패했습니다."),
	MALFORMED_JSON(HttpStatus.BAD_REQUEST, "P005", "구매 정보 JSON 형식이 올바르지 않습니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C500", "서버 내부 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
