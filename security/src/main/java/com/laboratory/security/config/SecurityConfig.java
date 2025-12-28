package com.laboratory.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.authorizeHttpRequests(
        registry -> registry
            /* [1] 인증된 사용자만 허용 */
            .requestMatchers("/a").authenticated()
            /* [2] 리멤버-미 사용자만 허용 */
            .requestMatchers("/b").rememberMe()
            /* [3] 익명 사용자만 허용 */
            .requestMatchers("/c").anonymous()
            /* [4] 완전 인증된 사용자만 허용 */
            .requestMatchers("/d").fullyAuthenticated()
    );

    httpSecurity.formLogin(
        Customizer.withDefaults()
    );

    httpSecurity.httpBasic(
        Customizer.withDefaults()
    );

    httpSecurity.csrf(
        AbstractHttpConfigurer::disable
    );

    return httpSecurity.build();
  }
}