package com.laboratory.url.api.controller;

import com.laboratory.url.common.codec.AesCipher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /api/purchases — 단말기 구매 정보 수신 E2E")
class PurchaseControllerTest {

    private static final String RAW_JSON = """
            {"orderId":"20260811-0001","productName":"아메리카노 (톨)","quantity":2,\
            "amount":9000,"discountRate":"10%","customerPhone":"+821012345678",\
            "memo":"C+ 등급 회원, 적립률 5%","purchasedAt":"2026-08-11T10:15:30"}""";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AesCipher aesCipher;

    @Nested
    @DisplayName("JSON 원문 요청 (해결하려는 문제 상황)")
    class RawJson {

        @Test
        @DisplayName("%와 +가 섞인 JSON 원문을 200으로 정확히 파싱한다")
        void acceptsRawJson() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(RAW_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.format").value("RAW_JSON"))
                    .andExpect(jsonPath("$.purchase.orderId").value("20260811-0001"))
                    .andExpect(jsonPath("$.purchase.productName").value("아메리카노 (톨)"))
                    .andExpect(jsonPath("$.purchase.quantity").value(2))
                    .andExpect(jsonPath("$.purchase.amount").value(9000))
                    // % 가 hex 오류를 내지 않고 원문 그대로 보존
                    .andExpect(jsonPath("$.purchase.discountRate").value("10%"))
                    // + 가 공백으로 치환되지 않음
                    .andExpect(jsonPath("$.purchase.customerPhone").value("+821012345678"))
                    .andExpect(jsonPath("$.purchase.memo").value("C+ 등급 회원, 적립률 5%"));
        }

        @Test
        @DisplayName("charset 선언이 없어도 한글이 깨지지 않는다")
        void keepsKoreanWithoutCharsetDeclaration() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(RAW_JSON.getBytes(StandardCharsets.UTF_8)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.purchase.productName").value("아메리카노 (톨)"))
                    .andExpect(jsonPath("$.purchase.memo").value("C+ 등급 회원, 적립률 5%"));
        }

        @Test
        @DisplayName("charset을 ISO-8859-1로 잘못 선언해도 한글이 깨지지 않는다")
        void keepsKoreanDespiteWrongCharsetDeclaration() throws Exception {
            // 실제로 이렇게 보내는 단말기가 있다. 선언을 따라 읽으면 한글이 깨지므로 UTF-8로 고정해 읽는다.
            mockMvc.perform(post("/api/purchases")
                            .header("Content-Type", "text/plain; charset=ISO-8859-1")
                            .content(RAW_JSON.getBytes(StandardCharsets.UTF_8)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.purchase.productName").value("아메리카노 (톨)"))
                    .andExpect(jsonPath("$.purchase.customerPhone").value("+821012345678"));
        }

        @Test
        @DisplayName("Content-Type이 text/plain이어도 동일하게 처리한다")
        void acceptsRawJsonAsTextPlain() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(RAW_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.format").value("RAW_JSON"))
                    .andExpect(jsonPath("$.purchase.discountRate").value("10%"))
                    .andExpect(jsonPath("$.purchase.customerPhone").value("+821012345678"));
        }
    }

    @Nested
    @DisplayName("Content-Type 이 form-urlencoded 인 경우")
    class FormUrlEncodedContentType {

        // @RequestBody String 을 쓰면 Spring 이 본문 대신 파라미터 맵에서 본문을 재구성하기 때문에
        // JSON 원문은 "Required request body is missing" 으로 500 이 났다.
        // 컨트롤러가 원시 입력 스트림을 직접 읽도록 바꿔 해결한 회귀 케이스.

        @Test
        @DisplayName("JSON 원문을 500이 아닌 200으로 처리한다")
        void acceptsRawJsonAsFormUrlEncoded() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .content(RAW_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.format").value("RAW_JSON"))
                    .andExpect(jsonPath("$.purchase.discountRate").value("10%"))
                    .andExpect(jsonPath("$.purchase.customerPhone").value("+821012345678"));
        }

        @Test
        @DisplayName("Base64 암호문의 '+'가 훼손되지 않는다")
        void keepsPlusInCipherText() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .content(aesCipher.encryptToBase64(RAW_JSON)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.format").value("ENCRYPTED"))
                    .andExpect(jsonPath("$.purchase.customerPhone").value("+821012345678"));
        }
    }

    @Nested
    @DisplayName("URL 인코딩된 JSON 요청")
    class UrlEncodedJson {

        @Test
        @DisplayName("퍼센트 인코딩을 풀어 파싱한다")
        void acceptsUrlEncodedJson() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(percentEncode(RAW_JSON)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.format").value("URL_ENCODED_JSON"))
                    .andExpect(jsonPath("$.purchase.discountRate").value("10%"))
                    .andExpect(jsonPath("$.purchase.customerPhone").value("+821012345678"));
        }
    }

    @Nested
    @DisplayName("정상 규격(AES 암호문) 요청")
    class Encrypted {

        @Test
        @DisplayName("Base64 암호문을 복호화해 파싱한다")
        void acceptsEncrypted() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(aesCipher.encryptToBase64(RAW_JSON)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.format").value("ENCRYPTED"))
                    .andExpect(jsonPath("$.purchase.discountRate").value("10%"))
                    .andExpect(jsonPath("$.purchase.customerPhone").value("+821012345678"));
        }

        @Test
        @DisplayName("URL 인코딩된 Base64 암호문도 복호화한다")
        void acceptsUrlEncodedCipherText() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(percentEncode(aesCipher.encryptToBase64(RAW_JSON))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.format").value("ENCRYPTED"))
                    .andExpect(jsonPath("$.purchase.customerPhone").value("+821012345678"));
        }
    }

    @Nested
    @DisplayName("오류 응답")
    class Errors {

        @Test
        @DisplayName("해석 불가 페이로드는 400과 에러코드를 반환한다")
        void rejectsGarbage() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("이건 그냥 평문입니다!!"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("P002"));
        }

        @Test
        @DisplayName("quantity/amount 누락 시 400 P005 — 필수 필드는 get*으로 꺼낸다")
        void rejectsMissingPrimitiveField() throws Exception {
            // 필수/선택 구분이 PurchaseService의 get*/opt* 호출에 명시돼 있다.
            // 프레임워크 기본값(Jackson의 FAIL_ON_NULL_FOR_PRIMITIVES 등)에 의존하지 않는다.
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"orderId\":\"A-1\",\"discountRate\":\"10%\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("P005"));
        }

        @Test
        @DisplayName("JSON 형태지만 구조가 깨졌으면 400 P005")
        void rejectsMalformedJson() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("{\"orderId\": }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("P005"));
        }
    }

    private static String percentEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
