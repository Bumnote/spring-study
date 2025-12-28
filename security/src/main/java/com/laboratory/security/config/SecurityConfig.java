package com.laboratory.security.config;

import static org.springframework.http.HttpMethod.GET;

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
            .requestMatchers(GET, "/users")
            .hasAnyRole("USER", "ADMIN")
            .anyRequest().authenticated()
            .requestMatchers("/users")
            .hasRole("ADMIN")
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