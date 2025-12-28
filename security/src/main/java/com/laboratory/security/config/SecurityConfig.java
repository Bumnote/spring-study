package com.laboratory.security.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAuthority;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;
import static org.springframework.security.authorization.AuthorizationManagers.anyOf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  /* [1] 역할 계층 생성 */
  @Bean
  static RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("""
        ROLE_SUPERVISOR > ROLE_MANAGER > ROLE_EMPLOYEE""");
  }

  /* [2] 역할 계층 bean 객체를 주입 */
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  /* [1] 권한 기반 인가 매니저에게 역할 계층 규칙 지정 */
  private <T> AuthorizationManager<T> hasAuthorityWithRoleHierarchy(String
      authority) {
    var manager = hasAuthority(authority);
    manager.setRoleHierarchy(roleHierarchy());
    return (AuthorizationManager<T>) manager;
  }

  /* [2] 역할 기반 인가 매니저에게 역할 계층 규칙 지정 */
  private <T> AuthorizationManager<T> hasRoleWithRoleHierarchy(String role) {
    var manager = hasRole(role);
    manager.setRoleHierarchy(roleHierarchy());
    return (AuthorizationManager<T>) manager;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(
        registry -> registry
            .requestMatchers("/v1/management/**").denyAll()
            .requestMatchers("/v2/management/admin").permitAll()
            .requestMatchers(GET, "/v2/management/employees").authenticated()
            .requestMatchers(GET, "/v2/management/employees/**")
            /*
            [3] MANAGEMENT::READ 권한, EMPLOYEE 역할 이상
            사용자만 접근 가능
            */
            .access(
                anyOf(
                    hasAuthorityWithRoleHierarchy("MANAGEMENT::READ "),
                    hasRoleWithRoleHierarchy("EMPLOYEE")
                )
            )
            .requestMatchers("/v2/management/employees/**")
            /*
            [4] MANAGEMENT:: WRITE 권한, MANAGER 역할
            이상 사용자만 접근 가능
            */
            .access(
                anyOf(
                    hasAuthorityWithRoleHierarchy("MANAGEMENT::WRITE "),
                    hasRoleWithRoleHierarchy("MANAGER")
                )
            )
            .anyRequest().denyAll()
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