package com.laboratory.security.passwordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class BCryptPasswordEncoderTest {

  @DisplayName("BCryptPasswordEncoder 인코딩 및 매칭 테스트")
  @Test
  void BCryptPasswordEncoderEncodeAndMatchTest() {

    // given
    var sut = new BCryptPasswordEncoder();

    // when
    var result = sut.encode("hello world");

    // then
    assertTrue(sut.matches("hello world", result)); // 인코딩된 값을 비교
    System.out.println(result);
  }
}
