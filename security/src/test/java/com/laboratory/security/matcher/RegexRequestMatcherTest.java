package com.laboratory.security.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;

class RegexRequestMatcherTest {

  @DisplayName("RegexRequestMatcher ID 패턴 매칭 테스트")
  @ParameterizedTest
  @CsvSource(value = {
      "/boards/1,true",
      "/boards/11312,true",
      "/board/11312,false",
      "/boards/123a,false",
      "/boards/!23,false",
  }, delimiterString = ",")
  void id_pattern_test(String urlPath, boolean expectedResult) {

    // given
    var httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.setServletPath(urlPath);

    // when -> 정규식 규칙 지정
    var sut = regexMatcher("^/boards/\\d+$");

    // then
    assertEquals(expectedResult, sut.matches(httpServletRequest));
  }

  @DisplayName("RegexRequestMatcher 날짜 패턴 매칭 테스트")
  @ParameterizedTest
  @CsvSource(value = {
      "/boards?start=20230531&end=20230630,true",
      "/boards?start=20231201&end=20240101,true",
      "/boards?start=20240101&end=20241231,true",
      "/board?start=20230531&end=20230630,false",
      "/boards?start=20231301&end=20240101,false",
      "/boards?start=20231232&end=20240101,false",
      "/boards?start=2023012!&end=20240101,false",
      "/boards?start=20231201&end=20240132,false",
      "/boards?start=20231201&end=20241301,false",
      "/boards?start=20231201&end=2024010!,false",
  }, delimiterString = ",")
  void date_pattern_test(String urlPath, boolean expectedResult) {

    // given
    var httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.setServletPath(urlPath);
    var dateRegex = "(\\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01]))";

    // when -> 정규식 규칙 지정
    var sut = regexMatcher("^/boards\\?start=" + dateRegex + "&end=" + dateRegex + "$");

    // then
    assertEquals(expectedResult, sut.matches(httpServletRequest));
  }
}
