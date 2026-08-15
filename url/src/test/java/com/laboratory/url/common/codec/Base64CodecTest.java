package com.laboratory.url.common.codec;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Base64Codec — 단말기별 Base64 방언 흡수")
class Base64CodecTest {

	@Test
	@DisplayName("표준 Base64를 디코딩한다")
	void decodesStandard() {
		assertThat(new String(Base64Codec.decode("7ISc7Jq4"), StandardCharsets.UTF_8)).isEqualTo("서울");
	}

	@Test
	@DisplayName("URL-safe 알파벳(-, _)도 디코딩한다")
	void decodesUrlSafe() {
		byte[] bytes = {(byte)0xFB, (byte)0xFF, 0x00};
		String urlSafe = java.util.Base64.getUrlEncoder().encodeToString(bytes);

		assertThat(Base64Codec.decode(urlSafe)).isEqualTo(bytes);
	}

	@Test
	@DisplayName("패딩이 빠져도 복원한다")
	void decodesWithoutPadding() {
		assertThat(new String(Base64Codec.decode("YWJjZGU"), StandardCharsets.UTF_8)).isEqualTo("abcde");
	}

	@Test
	@DisplayName("MIME 줄바꿈(\\r\\n)은 제거하고 복원한다")
	void decodesWithLineBreaks() {
		assertThat(new String(Base64Codec.decode("YWJj\r\nZGU="), StandardCharsets.UTF_8)).isEqualTo("abcde");
	}

	@Test
	@DisplayName("앞뒤 여백은 제거한다")
	void decodesWithSurroundingWhitespace() {
		assertThat(new String(Base64Codec.decode("  YWJjZGU=\n"), StandardCharsets.UTF_8)).isEqualTo("abcde");
	}

	@Nested
	@DisplayName("'+' 가 공백으로 훼손된 Base64 복구")
	class PlusCorruptedByUpstream {

		// Base64 알파벳에 공백이 없으므로, 문자열 중간의 공백은 '+' 가 치환된 흔적일 수밖에 없다.
		// 레거시 URLDecoder / 서블릿 폼 파싱 / 일부 프록시가 이 훼손을 일으킨다.

		@Test
		@DisplayName("중간 공백을 '+' 로 되돌려 원래 바이트를 복원한다")
		void restoresSpaceToPlus() {
			byte[] original = {(byte)0xFB, (byte)0xFF, 0x00, (byte)0xFB, (byte)0xFF, 0x00};
			String base64 = Base64Codec.encode(original);
			assertThat(base64).contains("+"); // 전제 확인

			String corrupted = base64.replace('+', ' ');

			assertThat(Base64Codec.decode(corrupted)).isEqualTo(original);
		}

		@Test
		@DisplayName("공백을 제거해버리면 길이가 어긋나 복호화가 깨진다 (수정 전 동작 대조)")
		void strippingSpaceWouldBreak() {
			byte[] original = {(byte)0xFB, (byte)0xFF, 0x00, (byte)0xFB, (byte)0xFF, 0x00};
			String corrupted = Base64Codec.encode(original).replace('+', ' ');

			// 수정 전처럼 공백을 '제거'하면 바이트가 어긋난다.
			String stripped = corrupted.replace(" ", "");

			assertThat(Base64Codec.decode(corrupted)).isEqualTo(original);          // 수정 후
			assertThat(Base64Codec.decode(stripped)).isNotEqualTo(original);        // 수정 전 방식
		}
	}

	@ParameterizedTest(name = "[{index}] {0}")
	@DisplayName("Base64가 아닌 문자열은 isBase64가 false")
	@ValueSource(strings = {"{\"orderId\":\"A-1\"}", "한글 원문", "!!!!"})
	void detectsNonBase64(String value) {
		assertThat(Base64Codec.isBase64(value)).isFalse();
	}
}
