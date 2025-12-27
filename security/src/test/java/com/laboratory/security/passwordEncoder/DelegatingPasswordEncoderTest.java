package com.laboratory.security.passwordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;

class DelegatingPasswordEncoderTest {

  @DisplayName("DelegatingPasswordEncoder 인코딩 및 매칭 테스트")
  @ParameterizedTest
  @ValueSource(strings = {
      "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG",
      "{noop}password",
      "{pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc",
      "{scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=",
      "{sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0"
  })
  void DelegatingPasswordEncoderEncodeAndMatchTest(String encodedValue) {

    // given
    var sut = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    // when & then
    assertTrue(sut.matches("password", encodedValue));
  }

  @DisplayName("DelegatingPasswordEncoder Id 없는 경우, IllegalArgumentException 예외 테스트")
  @Test
  void withoutIdThenThrowTest() {

    // given
    var sut = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    // when & then
    assertThrows(IllegalArgumentException.class,
        () -> sut.matches(
            "password",
            "\"$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"
        )
    );
  }

  @DisplayName("DelegatingPasswordEncoder 기본 인코더 변경 테스트")
  @Test
  void changeDefaultPasswordEncoderTest() {

    // given
    var sut = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();

    // when
    sut.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());

    // then
    assertTrue(
        sut.matches(
            "password",
            "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"
        )
    );
  }
}
