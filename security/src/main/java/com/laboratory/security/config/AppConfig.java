package com.laboratory.security.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.util.StringUtils;

@Configuration
public class AppConfig {

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository
  ) {
    /* [1] OAuth2AuthorizedClientManager 인스턴스 생성 */
    var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
        clientRegistrationRepository,
        authorizedClientRepository
    );

    /* [2] OAuth2AuthorizedClientProvider 인스턴스 생성 */
    var authorizedClientProvider =
        OAuth2AuthorizedClientProviderBuilder.builder()
            .password()
            .build();

    /* [3] OAuth2AuthorizedClientProvider 등록 */
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    /* [4] 컨텍스트 속성 맵퍼 등록 */
    authorizedClientManager.setContextAttributesMapper(contextAttributesMapper());

    return authorizedClientManager;
  }

  private Function<OAuth2AuthorizeRequest, Map<String, Object>> contextAttributesMapper() {
    return authorizeRequest -> {
      Map<String, Object> contextAttributes = Collections.emptyMap();
      /* [1] HttpServletRequest 인스턴스 획득 */
      HttpServletRequest servletRequest = authorizeRequest.getAttribute(HttpServletRequest.class.getName());
      if (servletRequest == null) {
        return contextAttributes;
      }
      /* [2] 요청 정보로부터 사용자 이름, 비밀번호 획득 */
      String username = servletRequest.getParameter(OAuth2ParameterNames.USERNAME);
      String password = servletRequest.getParameter(OAuth2ParameterNames.PASSWORD);
      if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
        /* [3] 아이디와 비밀번호를 속성 맵에 담아서 반환 */
        contextAttributes = new HashMap<>();
        contextAttributes.put(
            OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME,
            username
        );
        contextAttributes.put(
            OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME,
            password
        );
      }

      return contextAttributes;
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    var inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
    inMemoryUserDetailsManager.createUser(
        User.builder()
            .username("junhyunny")
            .password(passwordEncoder().encode("12345"))
            .roles("USER")
            .build()
    );

    return inMemoryUserDetailsManager;
  }
}