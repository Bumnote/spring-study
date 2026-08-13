package com.laboratory.url.api.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laboratory.url.api.dto.PurchaseResponse;
import com.laboratory.url.api.service.PurchaseService;
import com.laboratory.url.common.exception.ErrorCode;
import com.laboratory.url.common.exception.PayloadException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchases")
public class PurchaseController {

	private final PurchaseService purchaseService;

	@PostMapping(consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public PurchaseResponse receive(HttpServletRequest request) {
		return purchaseService.receive(readRawBody(request));
	}

	private String readRawBody(HttpServletRequest request) {
		try {
			return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new PayloadException(ErrorCode.EMPTY_PAYLOAD, e);
		}
	}
}
