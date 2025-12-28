package com.laboratory.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class AppConfig {

  @Bean
  public UserDetailsService userDetailsService() {
    var userDetailsService = new InMemoryUserDetailsManager();

    userDetailsService.createUser(
        /* [1] junhyunny 사용자 등록 */
        User.withDefaultPasswordEncoder()
            .username("junhyunny")
            .password("12345")
            .build()
    );

    userDetailsService.createUser(
        /* [2] jua 사용자 등록 */
        User.withDefaultPasswordEncoder()
            .username("jua")
            .password("12345")
            .accountExpired(true)
            .build()
    );

    return userDetailsService;
  }
}