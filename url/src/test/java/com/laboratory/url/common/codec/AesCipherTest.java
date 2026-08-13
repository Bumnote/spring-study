package com.laboratory.url.common.codec;

import com.laboratory.url.common.config.AesProperties;
import com.laboratory.url.common.exception.ErrorCode;
import com.laboratory.url.common.exception.PayloadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AesCipher — AES/CBC 암복호화")
class AesCipherTest {

    private static final String KEY = "0123456789abcdef0123456789abcdef"; // 32 bytes

    private final AesCipher aesCipher = new AesCipher(new AesProperties(KEY));

    @Test
    @DisplayName("암호화 후 복호화하면 원문이 그대로 복원된다")
    void roundTrip() {
        String plainText = "{\"orderId\":\"A-1\",\"discountRate\":\"10%\",\"customerPhone\":\"+821012345678\"}";

        String encrypted = aesCipher.encryptToBase64(plainText);

        assertThat(aesCipher.decryptFromBase64(encrypted)).isEqualTo(plainText);
    }

    @Test
    @DisplayName("한글이 포함된 원문도 복원된다")
    void roundTripWithKorean() {
        String plainText = "{\"productName\":\"아메리카노 (톨)\",\"memo\":\"매장 식사\"}";

        assertThat(aesCipher.decryptFromBase64(aesCipher.encryptToBase64(plainText))).isEqualTo(plainText);
    }

    @Test
    @DisplayName("IV가 매번 달라 같은 평문도 매번 다른 암호문이 된다")
    void randomIvProducesDifferentCipherText() {
        String plainText = "{\"orderId\":\"A-1\"}";

        assertThat(aesCipher.encryptToBase64(plainText))
                .isNotEqualTo(aesCipher.encryptToBase64(plainText));
    }

    @Test
    @DisplayName("키 길이가 16/24/32바이트가 아니면 기동 시점에 막는다")
    void rejectsInvalidKeyLength() {
        assertThatThrownBy(() -> new AesCipher(new AesProperties("tooshort")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("16/24/32");
    }

    @Test
    @DisplayName("IV 길이에 못 미치는 입력은 PayloadException으로 변환된다")
    void rejectsTooShortInput() {
        String tooShort = Base64Codec.encode(new byte[10]);

        assertThatThrownBy(() -> aesCipher.decryptFromBase64(tooShort))
                .isInstanceOf(PayloadException.class)
                .extracting(e -> ((PayloadException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYLOAD_DECRYPT_FAILED);
    }

    @Test
    @DisplayName("다른 키로 복호화하면 PayloadException이 발생한다")
    void rejectsWrongKey() {
        String encrypted = aesCipher.encryptToBase64("{\"orderId\":\"A-1\"}");
        AesCipher otherCipher = new AesCipher(new AesProperties("fedcba9876543210fedcba9876543210"));

        assertThatThrownBy(() -> otherCipher.decryptFromBase64(encrypted))
                .isInstanceOf(PayloadException.class);
    }
}
