package com.laboratory.url.common.codec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SafeUrlDecoder — URLDecoder의 % / + 문제 해결")
class SafeUrlDecoderTest {

    @Nested
    @DisplayName("문제 1: % 문자 hex 오류")
    class PercentCharacter {

        @Test
        @DisplayName("URLDecoder는 '10%'에서 IllegalArgumentException을 던진다 (재현)")
        void urlDecoder_throws_onInvalidHex() {
            String rawJson = "{\"discountRate\":\"10%\"}";

            assertThatThrownBy(() -> URLDecoder.decode(rawJson, StandardCharsets.UTF_8))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Illegal hex characters");
        }

        @Test
        @DisplayName("SafeUrlDecoder는 예외 없이 원문을 그대로 보존한다")
        void safeUrlDecoder_keepsLiteralPercent() {
            String rawJson = "{\"discountRate\":\"10%\"}";

            assertThat(SafeUrlDecoder.decode(rawJson)).isEqualTo(rawJson);
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @DisplayName("%뒤가 16진수 두 자리가 아니면 리터럴 %로 취급한다")
        @ValueSource(strings = {
                "100%",                         // 문자열 끝의 %
                "50% 할인",                      // % 뒤 공백
                "%zz",                          // 16진수가 아닌 문자
                "%2",                           // 자릿수 부족
                "a%b%c",                        // 중간중간 깨진 %
                "{\"memo\":\"수수료 3%, 부가세 10%\"}"
        })
        void safeUrlDecoder_neverThrows(String value) {
            assertThatCode(() -> SafeUrlDecoder.decode(value)).doesNotThrowAnyException();
            assertThat(SafeUrlDecoder.decode(value)).isEqualTo(value);
        }

        @Test
        @DisplayName("깨진 %와 정상 %XX가 섞여 있으면 정상 이스케이프만 푼다")
        void safeUrlDecoder_decodesValidEscapesOnly() {
            // %25 → '%', 뒤의 '%' 는 리터럴
            assertThat(SafeUrlDecoder.decode("a%25b%c")).isEqualTo("a%b%c");
        }
    }

    @Nested
    @DisplayName("문제 2: + 문자가 공백으로 치환")
    class PlusCharacter {

        @Test
        @DisplayName("URLDecoder는 국제전화번호의 +를 공백으로 바꾼다 (재현)")
        void urlDecoder_convertsPlusToSpace() {
            String rawJson = "{\"customerPhone\":\"+821012345678\"}";

            String decoded = URLDecoder.decode(rawJson, StandardCharsets.UTF_8);

            assertThat(decoded).isEqualTo("{\"customerPhone\":\" 821012345678\"}");
            assertThat(decoded).doesNotContain("+");
        }

        @Test
        @DisplayName("SafeUrlDecoder는 +를 그대로 보존한다")
        void safeUrlDecoder_preservesPlus() {
            String rawJson = "{\"customerPhone\":\"+821012345678\"}";

            assertThat(SafeUrlDecoder.decode(rawJson)).isEqualTo(rawJson);
        }

        @Test
        @DisplayName("Base64 알파벳의 +와 /가 훼손되지 않는다")
        void safeUrlDecoder_preservesBase64Alphabet() {
            String base64 = "ab+cd/ef+gh==";

            assertThat(SafeUrlDecoder.decode(base64)).isEqualTo(base64);
            assertThat(URLDecoder.decode(base64, StandardCharsets.UTF_8)).isNotEqualTo(base64); // 대조
        }

        @Test
        @DisplayName("이스케이프가 섞여 있어도 +는 여전히 보존한다")
        void safeUrlDecoder_preservesPlusEvenWhenEscapesExist() {
            // %20만 공백이 되고, '+'는 '+' 그대로
            assertThat(SafeUrlDecoder.decode("a+b%20c")).isEqualTo("a+b c");
        }

        @Test
        @DisplayName("공백은 %20으로만 해석한다")
        void safeUrlDecoder_decodesSpaceOnlyFromPercent20() {
            assertThat(SafeUrlDecoder.decode("%20")).isEqualTo(" ");
        }
    }

    @Nested
    @DisplayName("정상 퍼센트 인코딩 디코딩")
    class NormalDecoding {

        @Test
        @DisplayName("URL 인코딩된 JSON을 원문으로 되돌린다")
        void decodesEncodedJson() {
            String encoded = "%7B%22orderId%22%3A%22A-1%22%7D";

            assertThat(SafeUrlDecoder.decode(encoded)).isEqualTo("{\"orderId\":\"A-1\"}");
        }

        @Test
        @DisplayName("멀티바이트 UTF-8(한글)을 정상 복원한다")
        void decodesMultiByteUtf8() {
            assertThat(SafeUrlDecoder.decode("%ED%95%9C%EA%B8%80")).isEqualTo("한글");
        }

        @Test
        @DisplayName("한글 + 이모지가 섞인 인코딩도 URLDecoder와 동일한 결과를 낸다")
        void matchesUrlDecoderForWellFormedInput() {
            String original = "상품명 테스트 🚀";
            String encoded = java.net.URLEncoder.encode(original, StandardCharsets.UTF_8)
                    .replace("+", "%20"); // 폼 규격의 '+' 공백을 %20으로 정규화

            assertThat(SafeUrlDecoder.decode(encoded)).isEqualTo(original);
        }

        @Test
        @DisplayName("%XX가 없는 문자열은 그대로 반환한다")
        void returnsAsIsWhenNoEscape() {
            String value = "{\"orderId\":\"A-1\"}";

            assertThat(SafeUrlDecoder.decode(value)).isSameAs(value);
        }

        @Test
        @DisplayName("null과 빈 문자열을 안전하게 처리한다")
        void handlesNullAndEmpty() {
            assertThat(SafeUrlDecoder.decode(null)).isNull();
            assertThat(SafeUrlDecoder.decode("")).isEmpty();
        }
    }

}
