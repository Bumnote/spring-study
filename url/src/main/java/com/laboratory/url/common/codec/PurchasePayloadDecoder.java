package com.laboratory.url.common.codec;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Component;

import com.laboratory.url.common.exception.ErrorCode;
import com.laboratory.url.common.exception.PayloadException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PurchasePayloadDecoder {

	private final AesCipher aesCipher;

	public DecodedPayload decode(String payload) {
		if (payload.isBlank()) {
			throw new PayloadException(ErrorCode.EMPTY_PAYLOAD);
		}

		JSONObject json = parseOrNull(payload);
		if (json != null) {
			return decoded(PayloadFormat.RAW_JSON, json);
		}

		String urlDecoded = SafeUrlDecoder.decode(payload);

		json = parseOrNull(urlDecoded);
		if (json != null) {
			return decoded(PayloadFormat.URL_ENCODED_JSON, json);
		}

		if (!Base64Codec.isBase64(urlDecoded)) {
			throw unresolvable(urlDecoded);
		}

		String decrypted = aesCipher.decryptFromBase64(urlDecoded);

		json = parseOrNull(decrypted);
		if (json == null) {
			throw new PayloadException(ErrorCode.UNRESOLVABLE_PAYLOAD,
				"복호화 결과가 JSON이 아닙니다. 앞 32자=" + preview(decrypted));
		}
		return decoded(PayloadFormat.ENCRYPTED, json);
	}

	private JSONObject parseOrNull(String value) {
		try {
			JSONTokener tokener = new JSONTokener(value);
			JSONObject json = new JSONObject(tokener);
			return tokener.nextClean() == 0 ? json : null;
		} catch (JSONException e) {
			return null;
		}
	}

	private PayloadException unresolvable(String value) {
		String trimmed = value.strip();
		if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
			return new PayloadException(ErrorCode.MALFORMED_JSON, "앞 32자=" + preview(value));
		}
		return new PayloadException(ErrorCode.UNRESOLVABLE_PAYLOAD,
			"JSON도 Base64도 아닙니다. 앞 32자=" + preview(value));
	}

	private DecodedPayload decoded(PayloadFormat format, JSONObject json) {
		log.debug("페이로드 형태: {}", format.getDescription());
		return DecodedPayload.of(format, json);
	}

	private String preview(String value) {
		String trimmed = value.strip();
		return trimmed.length() <= 32 ? trimmed : trimmed.substring(0, 32) + "...";
	}
}
