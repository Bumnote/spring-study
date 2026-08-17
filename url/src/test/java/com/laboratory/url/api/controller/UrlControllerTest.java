package com.laboratory.url.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /api/url — URL 인코딩/디코딩 실습 API")
class UrlControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Nested
	@DisplayName("POST /api/url/decode")
	class Decode {

		@Test
		@DisplayName("퍼센트 인코딩된 한글을 원문으로 되돌린다")
		void decodesKorean() throws Exception {
			mockMvc.perform(postJson("/api/url/decode", "{\"value\":\"%EC%95%88%EB%85%95\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.input").value("%EC%95%88%EB%85%95"))
				.andExpect(jsonPath("$.decoded").value("안녕"));
		}

		@Test
		@DisplayName("URLDecoder는 '+'를 공백으로 바꾼다 — 전화번호가 훼손되는 지점")
		void turnsPlusIntoSpace() throws Exception {
			mockMvc.perform(postJson("/api/url/decode", "{\"value\":\"+821012345678\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.decoded").value(" 821012345678"));
		}
	}

	@Nested
	@DisplayName("POST /api/url/encode-decode")
	class EncodeDecode {

		@Test
		@DisplayName("인코딩 후 디코딩하면 원문으로 돌아온다")
		void roundTripsSuccessfully() throws Exception {
			mockMvc.perform(postJson("/api/url/encode-decode", "{\"value\":\"50%할인\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.input").value("50%할인"))
				.andExpect(jsonPath("$.encoded").value("50%25%ED%95%A0%EC%9D%B8"))
				.andExpect(jsonPath("$.decoded").value("50%할인"))
				.andExpect(jsonPath("$.roundTripSuccess").value(true));
		}
	}

	@Nested
	@DisplayName("POST /api/url/device")
	class Device {

		@Test
		@DisplayName("URL 인코딩된 JSON을 decode 후 복구한다")
		void recoversUrlEncodedJson() throws Exception {
			String rawData = "{\"value\": \"50%할인\"}";
			String urlEncodedJson = URLEncoder.encode(rawData, StandardCharsets.UTF_8);

			mockMvc.perform(postJson("/api/url/device", urlEncodedJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.parsedFrom").value("URL_ENCODED_JSON"))
				.andExpect(jsonPath("$.value").value("50%할인"));
		}

		@Test
		@DisplayName("JSON 원문은 디코딩 없이 그대로 파싱한다")
		void parsesRawJson() throws Exception {
			mockMvc.perform(postJson("/api/url/device", "{\"value\": \"50%할인\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.parsedFrom").value("JSON"))
				.andExpect(jsonPath("$.value").value("50%할인"));
		}
	}

	@Nested
	@DisplayName("표준 URLDecoder + @RequestBody 조합의 한계")
	class Limitations {

		// 아래 두 케이스는 /api/purchases 가 SafeUrlDecoder + 원시 스트림 읽기로 우회하는 지점이다.
		// 실습용 엔드포인트는 표준 API를 그대로 쓰므로 한계가 그대로 드러난다.

		@Test
		@DisplayName("JSON도 URL 인코딩도 아닌 본문은 URLDecoder 예외로 500이 된다")
		void failsOnBareTextContainingPercent() throws Exception {
			// URLDecoder: Illegal hex characters in escape (%) pattern
			mockMvc.perform(postJson("/api/url/device", "50%할인"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value("C500"));
		}

		@Test
		@DisplayName("form-urlencoded로 오면 본문이 파라미터로 소비돼 500이 된다")
		void failsOnFormUrlEncodedContentType() throws Exception {
			// Required request body is missing — @RequestBody String 이 본문 대신
			// 파라미터 맵에서 본문을 재구성하려다 실패한다.
			mockMvc.perform(post("/api/url/device")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.content("{\"value\": \"50%할인\"}"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value("C500"));
		}
	}

	private static MockHttpServletRequestBuilder postJson(String url, String content) {
		return post(url)
			.contentType(MediaType.APPLICATION_JSON)
			.content(content);
	}
}
