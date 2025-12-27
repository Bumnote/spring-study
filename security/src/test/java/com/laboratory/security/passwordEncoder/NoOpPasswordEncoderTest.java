package com.laboratory.security.passwordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

class NoOpPasswordEncoderTest {

  @DisplayName("NoOpPasswordEncoder 인코딩 및 매칭 테스트")
  @Test
  void NoOpPasswordEncoderEncodeAndMatchTest() {

    // given
    var sut = NoOpPasswordEncoder.getInstance();

    // when
    var result = sut.encode("hello world");

    // then
    assertTrue(sut.matches("hello world", result)); // 인코딩되지 않는 평문을 그대로 비교
    System.out.println(result);
  }
}
