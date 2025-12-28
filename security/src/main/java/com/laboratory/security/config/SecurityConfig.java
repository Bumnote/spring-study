package com.laboratory.security.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();

    /* [1] 허용 출처 등록 */
    configuration.setAllowedOrigins(List.of("http://allowed-origin.com:8080 "));

    /* [2] 허용 메서드 등록 */
    configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    /* [3] CORS 허용하는 경로 등록 */
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.authorizeHttpRequests(
        registry -> registry
            .anyRequest().authenticated()
    );

    httpSecurity.oauth2Login(
        configurer -> configurer
            .redirectionEndpoint(
                endpointConfig -> endpointConfig
                    .baseUri("/**/oauth2/code/**")
            )
            .defaultSuccessUrl("/")
    );

    httpSecurity.cors(
        configurer -> configurer
            .configurationSource(corsConfigurationSource())
    );

    httpSecurity.httpBasic(
        Customizer.withDefaults()
    );

    httpSecurity.csrf(
        Customizer.withDefaults()
    );

    return httpSecurity.build();
  }
}