package com.laboratory.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  static RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("""
        ROLE_ADMIN > ROLE_MANAGER > ROLE_USER > ROLE_VISITOR
        WRITE::HR > READ::HR"""
    );
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.authorizeHttpRequests(
        registry -> registry
            /* [1] ADMIN 역할 사용자만 접근 가능 */
            .requestMatchers("/a").hasRole("ADMIN")
            /*
            [2] ADMIN, USER 역할 중 하나라도
            해당하는 사용자는 접근 가능
            */
            .requestMatchers("/b").hasAnyRole("ADMIN", "USER")
            /* [3] WIRTE::HR 권한을 가진 사용자만 접근 가능 */
            .requestMatchers("/c").hasAuthority("WRITE::HR")
            /*
            [4] READ::HR, WRITE::HR 역할 중
            하나라도 가진 사용자는 접근 가능
            */
            .requestMatchers("/d").hasAnyRole("READ::HR", "WRITE::HR")
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