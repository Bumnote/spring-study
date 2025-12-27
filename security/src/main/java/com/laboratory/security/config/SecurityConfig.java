package com.laboratory.security.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.http.HttpMethod.GET;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    // 인가 규칙 지정
    httpSecurity.authorizeHttpRequests(
        registry -> registry
            .requestMatchers(toH2Console()).permitAll()
            .requestMatchers("/signup").permitAll()
            .requestMatchers(GET, "/login").permitAll()
            .anyRequest().authenticated()
    );

    // 폼 로그인 인증 설정
    httpSecurity.formLogin(
        configurer -> configurer
            .defaultSuccessUrl("/home")
            .loginPage("/login")
            .loginProcessingUrl("/login/processing")
            .failureUrl("/login?fail")
    );

    // 로그아웃 설정
    httpSecurity.logout(
        configurer -> configurer.logoutUrl("/logout/processing")
    );

    // CSRF 비활성화
    httpSecurity.csrf(
        AbstractHttpConfigurer::disable
    );

    // H2 콘솔을 위한 부가 설정
    httpSecurity.headers(
        configurer -> configurer.frameOptions(
            HeadersConfigurer.FrameOptionsConfig::disable
        )
    );

    return httpSecurity.build();
  }
}
