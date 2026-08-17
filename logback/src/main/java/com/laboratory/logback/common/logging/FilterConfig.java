package com.laboratory.logback.common.logging;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 서블릿 필터 등록 및 실행 우선순위 설정.
 *
 * TraceIdFilter 를 HIGHEST_PRECEDENCE 로 등록해 필터 체인에서 "가장 먼저" 실행되게 한다.
 * → 이후 실행되는 다른 필터/인터셉터/컨트롤러의 모든 로그에 traceId(MDC)가 포함된다.
 *
 * 참고: @Component + @Order 방식은 서블릿 필터 순서 보장이 애매할 수 있어,
 *       FilterRegistrationBean.setOrder() 로 명시적으로 우선순위를 지정한다.
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>(new TraceIdFilter());
        registration.setName("traceIdFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // 가장 먼저 실행
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilterRegistration() {
        FilterRegistrationBean<RequestResponseLoggingFilter> registration =
                new FilterRegistrationBean<>(new RequestResponseLoggingFilter());
        registration.setName("requestResponseLoggingFilter");
        registration.addUrlPatterns("/*");
        // TraceIdFilter(HIGHEST_PRECEDENCE) 바로 다음 → 요청/응답 로그에도 traceId가 포함되도록
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }
}
