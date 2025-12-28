package com.laboratory.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class AppConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService() {

    var userDetailsService = new InMemoryUserDetailsManager();
    /* 사용자 정보 준비 */
    userDetailsService.createUser(
        User.builder()
            .username("junhyunny")
            .password(passwordEncoder().encode("12345"))
            .roles("EMPLOYEE")
            .build()
    );

    return userDetailsService;
  }
}