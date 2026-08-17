package com.laboratory.logback.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 요청 단위로 traceId를 MDC에 심어, 하나의 요청에서 남는 모든 로그를 추적할 수 있게 한다.
 * logback-spring.xml에서 %X{traceId} / includeMdcKeyName 으로 로그에 노출된다.
 *
 * 등록과 실행 우선순위는 {@link FilterConfig} 의 FilterRegistrationBean 에서 관리한다.
 * (HIGHEST_PRECEDENCE 로 가장 먼저 실행되어야 이후 모든 로그에 traceId가 붙는다.)
 */
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = resolveTraceId(request);
            MDC.put(TRACE_ID, traceId);
            response.setHeader(TRACE_ID_HEADER, traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String inbound = request.getHeader(TRACE_ID_HEADER);
        if (inbound != null && !inbound.isBlank()) {
            return inbound;
        }
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
