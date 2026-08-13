package com.laboratory.url.common.codec;

import static lombok.AccessLevel.*;

import java.util.Base64;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Base64Codec {

	public static String encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static byte[] decode(String value) {
		String cleaned = value.replaceAll("[\\r\\n\\t\\f\\x0B]", "");

		if (cleaned.indexOf(' ') < 0) {
			return decodeStrict(cleaned);
		}

		// 공백은 두 가지로 해석될 수 있어 순서대로 시도한다.
		try {
			// 1순위 — 훼손된 '+' 로 본다.
			//   Base64 알파벳에 공백이 없으므로, 앞단(레거시 URLDecoder, 서블릿 폼 파싱, 프록시)이
			//   '+' 를 공백으로 바꿔놓았을 가능성이 높다. 이때 공백을 '제거'하면 길이가 줄어
			//   "Last unit does not have enough valid bits" 로 실패한다.
			//   앞뒤 공백도 함께 복원해야 한다 — Base64가 '+' 로 시작·종료할 수 있기 때문이다.
			return decodeStrict(cleaned.replace(' ', '+'));
		} catch (IllegalArgumentException e) {
			// 2순위 — 단순한 여백으로 본다. ('+' 로 바꾸면 길이/패딩이 어긋나 위에서 실패한다)
			return decodeStrict(cleaned.replace(" ", ""));
		}
	}

	private static byte[] decodeStrict(String value) {
		String normalized = value;
		if (normalized.indexOf('-') >= 0 || normalized.indexOf('_') >= 0) {
			// URL-safe 알파벳을 표준 알파벳으로 되돌린다.
			normalized = normalized.replace('-', '+').replace('_', '/');
		}
		return Base64.getDecoder().decode(withPadding(normalized));
	}

	/** Base64로 해석 가능한지 여부만 확인한다. */
	public static boolean isBase64(String value) {
		try {
			decode(value);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static String withPadding(String value) {
		int remainder = value.length() % 4;
		if (remainder == 0) {
			return value;
		}
		return value + "=".repeat(4 - remainder);
	}
}
