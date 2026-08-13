package com.laboratory.url.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PurchaseInfo(
	String orderId,
	String productName,
	int quantity,
	long amount,
	String discountRate,
	String customerPhone,
	String memo,
	String purchasedAt
) {
}
