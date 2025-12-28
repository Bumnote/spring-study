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
  public UserDetailsService inMemoryUserDetailsService(PasswordEncoder
      passwordEncoder) {
    var password = passwordEncoder.encode("123");
    var manager = new InMemoryUserDetailsManager();
    /* [1] junhyunny 사용자 */
    manager.createUser(
        User.builder()
            .username("junhyunny")
            .password(password)
            .roles("ADMIN")
            .build()
    );

    /* [2] tangerine 사용자 */
    manager.createUser(
        User.builder()
            .username("tangerine")
            .password(password)
            .roles("MANAGER")
            .build()
    );

    /* [3] jua 사용자 */
    manager.createUser(
        User.builder()
            .username("jua")
            .password(password)
            .roles("EMPLOYEE")
            .authorities("MANAGEMENT::WRITE")
            .build()
    );

    /* [4] tory 사용자 */
    manager.createUser(
        User.builder()
            .username("tory")
            .password(password)
            .roles("EMPLOYEE")
            .authorities("MANAGEMENT::READ")
            .build()
    );

    return manager;
  }
}