package com.laboratory.security.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.util.matcher.ELRequestMatcher;

class ELRequestMatcherTest {

  @DisplayName("ELRequestMatcher hasIpAddress 테스트")
  @ParameterizedTest
  @CsvSource(value = {
      /* [1] CIDR 방식이 아닌 경우 IP 일치 여부 */
      "192.168.0.1,192.168.0.1,true",
      "192.168.0.1,192.168.0.2,false",
      /* [2] CIDR 방식인 경우 IP 일치 여부 */
      "192.168.0.0/24,192.168.0.2,true",
      "192.168.0.0/24,192.168.0.255,true",
      "192.168.0.0/24,192.168.1.0,false",
  }, delimiterString = ",")
  void hasIpAddress(String ip, String requestIp, boolean expectedResult) {

    // given
    var httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.setRemoteAddr(requestIp);

    // when
    var sut = new ELRequestMatcher(String.format("hasIpAddress('%s')", ip));

    // then
    assertEquals(expectedResult, sut.matches(httpServletRequest));
  }
}
