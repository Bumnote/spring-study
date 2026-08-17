package com.laboratory.logback.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static net.logstash.logback.argument.StructuredArguments.value;

/**
 * 요청/응답을 구조적 JSON 로그로 남긴다 (method·uri·status·latency + body).
 *
 * 핵심: request body(스트림)는 한 번 읽으면 소비되므로, 컨트롤러가 읽어도 우리가 다시 읽을 수 있도록
 * ContentCachingRequestWrapper 로 감싼다. 응답도 ContentCachingResponseWrapper 로 감싸 body를 확보하되,
 * 마지막에 copyBodyToResponse() 로 실제 클라이언트에 본문을 반드시 되돌려줘야 한다(안 하면 빈 응답).
 *
 * StructuredArguments.value/keyValue 를 쓰면 각 값이 로그 메시지의 문자열이 아니라
 * JSON의 개별 필드(httpMethod, uri, requestBody ...)로 들어간다.
 */
@Slf4j
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    /** body가 너무 길면 잘라서 로깅 (로그 폭주 방지) */
    private static final int MAX_BODY_LENGTH = 2000;

    /** 요청 body를 메모리에 캐싱할 최대 바이트 (초과분은 캐싱 안 함 → 메모리 보호) */
    private static final int CONTENT_CACHE_LIMIT = 10_000;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request, CONTENT_CACHE_LIMIT);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        long startNs = System.nanoTime();
        try {
            filterChain.doFilter(cachingRequest, cachingResponse);
        } finally {
            long tookMs = (System.nanoTime() - startNs) / 1_000_000;
            logExchange(cachingRequest, cachingResponse, tookMs);
            // ★ 캐시된 응답 본문을 실제 응답으로 복사. 빠뜨리면 클라이언트가 빈 body를 받는다.
            cachingResponse.copyBodyToResponse();
        }
    }

    private void logExchange(ContentCachingRequestWrapper request,
                             ContentCachingResponseWrapper response,
                             long tookMs) {
        String uri = request.getRequestURI()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        log.info("HTTP {} {} -> {} ({}ms)",
                value("httpMethod", request.getMethod()),
                value("uri", uri),
                value("httpStatus", response.getStatus()),
                value("durationMs", tookMs),
                // 아래 둘은 메시지 placeholder에는 없지만 StructuredArguments라 JSON 필드로 들어간다
                keyValue("requestBody", body(request.getContentAsByteArray(), request.getContentType())),
                keyValue("responseBody", body(response.getContentAsByteArray(), response.getContentType())));
    }

    private String body(byte[] content, String contentType) {
        if (content == null || content.length == 0) {
            return null;
        }
        // JSON/텍스트 계열만 본문을 남기고, 바이너리(이미지·멀티파트 등)는 크기만 표기
        if (!isTextLike(contentType)) {
            return "(" + content.length + " bytes, " + contentType + ")";
        }
        String text = new String(content, StandardCharsets.UTF_8);
        return text.length() > MAX_BODY_LENGTH
                ? text.substring(0, MAX_BODY_LENGTH) + "...(truncated)"
                : text;
    }

    private boolean isTextLike(String contentType) {
        if (contentType == null) {
            return false;
        }
        String ct = contentType.toLowerCase();
        return ct.contains("json") || ct.contains("text")
                || ct.contains("xml") || ct.contains("x-www-form-urlencoded");
    }
}
