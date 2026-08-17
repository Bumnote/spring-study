package com.laboratory.url.api.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laboratory.url.api.dto.DecodeRequest;
import com.laboratory.url.api.dto.DecodeResponse;
import com.laboratory.url.api.dto.DeviceParseResponse;
import com.laboratory.url.api.dto.EncodeDecodeResponse;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * URL 인코딩/디코딩 동작을 눈으로 확인하기 위한 실습용 API.
 * <p>
 * 표준 {@link URLDecoder}를 그대로 쓴다. 실제 단말기 페이로드를 받는 경로는
 * {@code /api/purchases}이며, 그쪽은 {@code SafeUrlDecoder}로 '+'와 홀수 '%'를 보존한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/url")
public class UrlController {

	private final ObjectMapper objectMapper;

	@PostMapping("/decode")
	public DecodeResponse decode(@RequestBody DecodeRequest request) {
		String decoded = URLDecoder.decode(request.value(), StandardCharsets.UTF_8);
		return new DecodeResponse(request.value(), decoded);
	}

	@PostMapping("/encode-decode")
	public EncodeDecodeResponse encodeAndDecode(@RequestBody DecodeRequest request) {
		String input = request.value();
		String encoded = URLEncoder.encode(input, StandardCharsets.UTF_8);
		String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);

		return new EncodeDecodeResponse(input, encoded, decoded, input.equals(decoded));
	}

	@PostMapping("/device")
	public DeviceParseResponse parseDeviceBody(@RequestBody String rawBody) {
		try {
			DecodeRequest request = objectMapper.readValue(rawBody, DecodeRequest.class);
			return new DeviceParseResponse("JSON", request.value());
		} catch (JacksonException e) {
			String decodedBody = URLDecoder.decode(rawBody, StandardCharsets.UTF_8);
			DecodeRequest request = objectMapper.readValue(decodedBody, DecodeRequest.class);
			return new DeviceParseResponse("URL_ENCODED_JSON", request.value());
		}
	}
}
