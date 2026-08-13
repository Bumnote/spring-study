package com.laboratory.url.api.dto;

import com.laboratory.url.common.codec.PayloadFormat;

public record PurchaseResponse(
	PayloadFormat format,
	String formatDescription,
	PurchaseInfo purchase
) {
	public static PurchaseResponse of(PayloadFormat format, PurchaseInfo purchase) {
		return new PurchaseResponse(format, format.getDescription(), purchase);
	}
}
