package com.laboratory.security.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public AuthorizationEventPublisher authorizationEventPublisher(
      ApplicationEventPublisher applicationEventPublisher
  ) {
    return new SpringAuthorizationEventPublisher(applicationEventPublisher);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.authorizeHttpRequests(
        registry -> registry
            .requestMatchers("/v1/**").denyAll()
            .anyRequest().authenticated()
    );

    httpSecurity.httpBasic(
        Customizer.withDefaults()
    );

    return httpSecurity.build();
  }
}