package com.laboratory.url.common.codec;

import org.json.JSONObject;

public record DecodedPayload(
	PayloadFormat format,
	JSONObject json
) {

	public static DecodedPayload of(PayloadFormat format, JSONObject json) {
		return new DecodedPayload(format, json);
	}
}
