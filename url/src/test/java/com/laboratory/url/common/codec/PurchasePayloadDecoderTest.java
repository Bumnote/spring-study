package com.laboratory.url.common.codec;

import static org.assertj.core.api.Assertions.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.laboratory.url.common.config.AesProperties;
import com.laboratory.url.common.exception.ErrorCode;
import com.laboratory.url.common.exception.PayloadException;

@DisplayName("PurchasePayloadDecoder — 페이로드 형태 판별 후 복원")
class PurchasePayloadDecoderTest {

	/** %와 +를 동시에 포함하는, 문제가 되는 실제 구매 정보 JSON */
	private static final String RAW_JSON = """
		{"orderId":"20260811-0001","productName":"아메리카노 (톨)","quantity":2,\
		"amount":9000,"discountRate":"10%","customerPhone":"+821012345678",\
		"memo":"C+ 등급 회원, 적립률 5%","purchasedAt":"2026-08-11T10:15:30"}""";

	private final AesCipher aesCipher = new AesCipher(new AesProperties("0123456789abcdef0123456789abcdef"));
	private final PurchasePayloadDecoder decoder = new PurchasePayloadDecoder(aesCipher);

	/** %와 + 가 살아남았는지 — 이 프로젝트가 지키려는 핵심 불변식 */
	private void assertPayloadIntact(JSONObject json) {
		assertThat(json.getString("orderId")).isEqualTo("20260811-0001");
		assertThat(json.getInt("quantity")).isEqualTo(2);
		assertThat(json.getString("discountRate")).isEqualTo("10%");
		assertThat(json.getString("customerPhone")).isEqualTo("+821012345678");
		assertThat(json.getString("memo")).isEqualTo("C+ 등급 회원, 적립률 5%");
	}

	@Nested
	@DisplayName("JSON 원문으로 요청이 온 경우 (문제 상황)")
	class RawJsonPayload {

		@Test
		@DisplayName("기존 방식(URLDecoder 선적용)은 % 때문에 예외가 난다 — 재현")
		void legacyUrlDecoder_throws() {
			assertThatThrownBy(() -> URLDecoder.decode(RAW_JSON, StandardCharsets.UTF_8))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Illegal hex characters");
		}

		@Test
		@DisplayName("RAW_JSON으로 판별하고 % 와 + 를 훼손 없이 반환한다")
		void decodesRawJsonAsIs() {
			DecodedPayload decoded = decoder.decode(RAW_JSON);

			assertThat(decoded.format()).isEqualTo(PayloadFormat.RAW_JSON);
			assertPayloadIntact(decoded.json());
		}

		@Test
		@DisplayName("이미 인코딩된 것처럼 보이는 값(%25)도 JSON 원문이면 디코딩하지 않는다")
		void doesNotDecodeInsideRawJson() {
			// 사용자가 실제로 "100%25"라고 입력한 경우. 디코딩하면 "100%"가 되어 데이터가 훼손된다.
			String json = "{\"orderId\":\"A-1\",\"quantity\":1,\"amount\":100,\"memo\":\"쿠폰코드 100%25\"}";

			assertThat(decoder.decode(json).json().getString("memo")).isEqualTo("쿠폰코드 100%25");
		}

		@Test
		@DisplayName("앞뒤 공백/개행이 있어도 JSON 원문으로 인식한다")
		void handlesSurroundingWhitespace() {
			DecodedPayload decoded = decoder.decode("\n  " + RAW_JSON + "  \n");

			assertThat(decoded.format()).isEqualTo(PayloadFormat.RAW_JSON);
			assertPayloadIntact(decoded.json());
		}
	}

	@Nested
	@DisplayName("URL 인코딩된 JSON으로 요청이 온 경우")
	class UrlEncodedJsonPayload {

		@Test
		@DisplayName("퍼센트 인코딩을 풀어 원문을 복원한다")
		void decodesUrlEncodedJson() {
			DecodedPayload decoded = decoder.decode(percentEncode(RAW_JSON));

			assertThat(decoded.format()).isEqualTo(PayloadFormat.URL_ENCODED_JSON);
			assertPayloadIntact(decoded.json());
		}
	}

	@Nested
	@DisplayName("정상 규격(AES 암호문)으로 요청이 온 경우")
	class EncryptedPayload {

		@Test
		@DisplayName("Base64 암호문을 복호화한다")
		void decryptsBase64() {
			DecodedPayload decoded = decoder.decode(aesCipher.encryptToBase64(RAW_JSON));

			assertThat(decoded.format()).isEqualTo(PayloadFormat.ENCRYPTED);
			assertPayloadIntact(decoded.json());
		}

		@Test
		@DisplayName("Base64가 URL 인코딩되어 와도 복호화한다 (%2B, %2F, %3D)")
		void decryptsUrlEncodedBase64() {
			String payload = percentEncode(aesCipher.encryptToBase64(RAW_JSON));

			DecodedPayload decoded = decoder.decode(payload);

			assertThat(decoded.format()).isEqualTo(PayloadFormat.ENCRYPTED);
			assertPayloadIntact(decoded.json());
		}

		@Test
		@DisplayName("Base64의 +가 인코딩되지 않은 채 와도 공백으로 훼손되지 않는다")
		void keepsRawPlusInsideBase64() {
			// 단말기가 URL 인코딩을 생략하면 Base64의 '+'가 그대로 들어온다.
			// URLDecoder를 태웠다면 공백이 되어 복호화가 깨지는 케이스.
			String payload = encryptContaining("+");

			DecodedPayload decoded = decoder.decode(payload);

			assertThat(payload).contains("+");
			assertThat(decoded.format()).isEqualTo(PayloadFormat.ENCRYPTED);
			assertPayloadIntact(decoded.json());
		}

		@Test
		@DisplayName("상류에서 '+'가 공백으로 훼손된 Base64도 복구해 복호화한다")
		void recoversBase64WhosePlusBecameSpace() {
			// 레거시 URLDecoder / 서블릿 폼 파싱을 거치며 '+'가 공백이 된 페이로드.
			// 공백을 '제거'하면 길이가 어긋나 Base64 디코딩 자체가 실패한다.
			String corrupted = encryptContaining("+").replace('+', ' ');

			DecodedPayload decoded = decoder.decode(corrupted);

			assertThat(decoded.format()).isEqualTo(PayloadFormat.ENCRYPTED);
			assertPayloadIntact(decoded.json());
		}

		@Test
		@DisplayName("Base64 '선두'의 '+'가 공백으로 훼손돼도 복구한다")
		void recoversLeadingPlusTurnedIntoSpace() {
			// 파이프라인이 payload.strip()을 하면 이 선두 공백이 지워져 Base64Codec이 복원할 수 없다.
			String corrupted = encryptStartingWithPlus().replace('+', ' ');
			assertThat(corrupted).startsWith(" "); // 전제 확인

			DecodedPayload decoded = decoder.decode(corrupted);

			assertThat(decoded.format()).isEqualTo(PayloadFormat.ENCRYPTED);
			assertPayloadIntact(decoded.json());
		}

		@Test
		@DisplayName("MIME 줄바꿈이 섞인 Base64도 복호화한다")
		void decryptsLineWrappedBase64() {
			String payload = aesCipher.encryptToBase64(RAW_JSON);
			String wrapped = payload.substring(0, 20) + "\r\n" + payload.substring(20);

			assertPayloadIntact(decoder.decode(wrapped).json());
		}

		/** Base64 결과가 조건을 만족할 때까지 암호화를 반복한다 (IV가 매번 랜덤이므로 금방 나온다). */
		private String encryptContaining(String token) {
			for (int attempt = 0; attempt < 2000; attempt++) {
				String candidate = aesCipher.encryptToBase64(RAW_JSON);
				if (candidate.contains(token)) {
					return candidate;
				}
			}
			throw new IllegalStateException("'" + token + "'를 포함하는 Base64 암호문을 만들지 못했습니다.");
		}

		private String encryptStartingWithPlus() {
			for (int attempt = 0; attempt < 5000; attempt++) {
				String candidate = aesCipher.encryptToBase64(RAW_JSON);
				if (candidate.startsWith("+")) {
					return candidate;
				}
			}
			throw new IllegalStateException("'+'로 시작하는 Base64 암호문을 만들지 못했습니다.");
		}
	}

	@Nested
	@DisplayName("해석 불가 페이로드")
	class InvalidPayload {

		@Test
		@DisplayName("빈 본문은 EMPTY_PAYLOAD")
		void rejectsEmpty() {
			assertThatThrownBy(() -> decoder.decode("   "))
				.isInstanceOf(PayloadException.class)
				.extracting(e -> ((PayloadException)e).getErrorCode())
				.isEqualTo(ErrorCode.EMPTY_PAYLOAD);
		}

		@Test
		@DisplayName("JSON도 Base64도 아니면 UNRESOLVABLE_PAYLOAD")
		void rejectsGarbage() {
			assertThatThrownBy(() -> decoder.decode("이건 그냥 평문입니다!!"))
				.isInstanceOf(PayloadException.class)
				.extracting(e -> ((PayloadException)e).getErrorCode())
				.isEqualTo(ErrorCode.UNRESOLVABLE_PAYLOAD);
		}

		@Test
		@DisplayName("JSON 배열은 구매 정보로 받지 않는다 (단건 엔드포인트)")
		void rejectsJsonArray() {
			assertThatThrownBy(() -> decoder.decode("[{\"orderId\":\"A-1\"}]"))
				.isInstanceOf(PayloadException.class);
		}

		@Test
		@DisplayName("페이로드가 두 번 write되어 이어붙으면 앞의 하나만 조용히 취하지 않는다")
		void rejectsConcatenatedPayloads() {
			// org.json 은 객체 하나를 읽고 뒤를 무시하므로, 확인하지 않으면 뒷 건이 조용히 사라진다.
			assertThatThrownBy(() -> decoder.decode(RAW_JSON + RAW_JSON))
				.isInstanceOf(PayloadException.class);
		}

		@Test
		@DisplayName("JSON 뒤에 잔여 데이터가 붙어 있으면 거부한다")
		void rejectsTrailingGarbage() {
			assertThatThrownBy(() -> decoder.decode(RAW_JSON + " 잔여데이터"))
				.isInstanceOf(PayloadException.class);
		}

		@Test
		@DisplayName("Base64이지만 우리 키로 풀리지 않으면 PayloadException으로 거부한다")
		void rejectsUndecryptableBase64() {
			// 패딩 검사를 우연히 통과하면 복호화 결과가 JSON이 아니어서 걸린다. 둘 중 무엇이든 거부가 정답.
			String base64 = Base64Codec.encode(new byte[64]);

			assertThatThrownBy(() -> decoder.decode(base64))
				.isInstanceOf(PayloadException.class)
				.extracting(e -> ((PayloadException)e).getErrorCode())
				.isIn(ErrorCode.PAYLOAD_DECRYPT_FAILED, ErrorCode.UNRESOLVABLE_PAYLOAD);
		}
	}

	/** 단말기가 하는 것과 동일한 퍼센트 인코딩(공백은 %20, '+'는 %2B). */
	private static String percentEncode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
	}
}
