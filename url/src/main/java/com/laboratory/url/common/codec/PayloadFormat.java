package com.laboratory.url.common.codec;

/**
 * 단말기가 실제로 어떤 형태로 보냈는지. 응답/로그에 실어 운영 중 분포를 관찰하기 위한 값이다.
 */
public enum PayloadFormat {

    /** JSON 원문. URL 디코딩을 건너뛰어야 하는 케이스. */
    RAW_JSON("JSON 원문"),

    /** URL 인코딩된 JSON. 안전 URL 디코딩만 거치면 된다. */
    URL_ENCODED_JSON("URL 인코딩된 JSON"),

    /** 정상 규격. URL 디코딩 → Base64 디코딩 → AES 복호화. */
    ENCRYPTED("AES 암호문(Base64)");

    private final String description;

    PayloadFormat(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
