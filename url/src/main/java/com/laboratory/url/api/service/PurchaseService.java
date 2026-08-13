package com.laboratory.url.api.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.laboratory.url.api.dto.PurchaseInfo;
import com.laboratory.url.api.dto.PurchaseResponse;
import com.laboratory.url.common.codec.DecodedPayload;
import com.laboratory.url.common.codec.PurchasePayloadDecoder;
import com.laboratory.url.common.exception.ErrorCode;
import com.laboratory.url.common.exception.PayloadException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

	private final PurchasePayloadDecoder payloadDecoder;

	public PurchaseResponse receive(String payload) {
		DecodedPayload decoded = payloadDecoder.decode(payload);
		PurchaseInfo purchase = toPurchaseInfo(decoded.json());

		log.info("구매 정보 수신: format={}, orderId={}", decoded.format(), purchase.orderId());
		return PurchaseResponse.of(decoded.format(), purchase);
	}

	private PurchaseInfo toPurchaseInfo(JSONObject json) {
		try {
			return new PurchaseInfo(
				json.getString("orderId"),
				json.optString("productName", null),
				json.getInt("quantity"),
				json.getLong("amount"),
				json.optString("discountRate", null),
				json.optString("customerPhone", null),
				json.optString("memo", null),
				json.optString("purchasedAt", null));
		} catch (JSONException e) {
			throw new PayloadException(ErrorCode.MALFORMED_JSON, e);
		}
	}
}
