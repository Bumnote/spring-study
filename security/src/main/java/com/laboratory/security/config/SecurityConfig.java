package com.laboratory.security.config;

import com.laboratory.security.domain.AnonymousUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(
        registry -> registry.anyRequest().permitAll()
    );

    /* 익명 인증인 경우 인증 주체로 사용할 객체 등록 */
    httpSecurity.anonymous(
        configurer -> configurer
            .principal(new AnonymousUser())
            .authorities("ANONYMOUS_USER")
    );

    return httpSecurity.build();
  }
}