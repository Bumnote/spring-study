package com.laboratory.url.api.dto;

public record EncodeDecodeResponse(
	String input,
	String encoded,
	String decoded,
	boolean roundTripSuccess
) {
}
