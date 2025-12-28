package com.laboratory.security.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@WebMvcTest
class MvcRequestMatcherTest {

  @Autowired
  HandlerMappingIntrospector introspector;

  @DisplayName("MvcRequestMatcher '?' 패턴 매칭 테스트")
  @ParameterizedTest
  @CsvSource(value = {
      "/com/test,true",
      "/com/tast,true",
      "/com/t?st,true",
      "/com//test,false",
      "/com/tst,false",
      "/com/t/st,false",
      "/com/test/,false",
      "/com/test.html,false",
      "/com/task,false",
  }, delimiterString = ",")
  void question_mark_pattern(String urlPath, boolean expectedResult) {

    // given
    var httpServletRequest = new MockHttpServletRequest();
    /* URL 설정을 requestURI 프로퍼티에 추가 */
    httpServletRequest.setRequestURI(urlPath);

    // when
    var sut = new MvcRequestMatcher(introspector, "/com/t?st");

    // then
    assertEquals(expectedResult, sut.matches(httpServletRequest));
  }

  @DisplayName("MvcRequestMatcher '*' 패턴 매칭 테스트")
  @ParameterizedTest
  @CsvSource(value = {
      "/com/ple,true",
      "/com/apple,true",
      "/com//apple,false",
      "/com/apple/,false",
      "/com/apple.html,false",
  }, delimiterString = ",")
  void single_asterisk_mark_pattern(String urlPath, boolean expectedResult) {

    // given
    var httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.setRequestURI(urlPath);

    // when
    var sut = new MvcRequestMatcher(introspector, "/com/*ple");

    // then
    assertEquals(expectedResult, sut.matches(httpServletRequest));
  }

  @DisplayName("MvcRequestMatcher '**' 패턴 하위 디렉터리 매칭 테스트")
  @ParameterizedTest
  @CsvSource(value = {
      "/com/apple,true",
      "/com/apple/,true",
      "/com/red/apple,true",
      "/com/red/apple.html,true",
  }, delimiterString = ",")
  void double_asterisk_mark_pattern_test(String urlPath, boolean expectedResult) {

    // given
    var httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.setRequestURI(urlPath);

    // when
    var sut = new MvcRequestMatcher(introspector, "/com/**");

    // then
    assertEquals(expectedResult, sut.matches(httpServletRequest));
  }

  @DisplayName("MvcRequestMatcher '**' 패턴 중간 삽입 시 예외 발생 테스트")
  @Test
  void double_asterisk_mark_between_path_throw_exception() {

    // given
    var httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.setRequestURI("/com/red/green/apple");

    // when
    var sut = new MvcRequestMatcher(introspector, "/com/**/apple");
    var throwable = assertThrows(RuntimeException.class, () -> sut.matches(httpServletRequest));

    // then
    assertEquals(
        "No more pattern data allowed after {*...} or ** pattern element",
        throwable.getMessage()
    );
  }
}
