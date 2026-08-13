package com.laboratory.url.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "url.aes")
public record AesProperties(String key) {
}
