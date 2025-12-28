package com.laboratory.security.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

class SpringExpressionLanguageTest {

  @DisplayName("SpEL 간단한 메시지 출력 테스트")
  @Test
  void simple_message() {

    // given -> 표현식 파싱
    ExpressionParser parser = new SpelExpressionParser();
    Expression sut = parser.parseExpression("'Hello World'");

    // when -> 표현 객체로부터 값을 추출
    String message = (String) sut.getValue();

    // then
    assertEquals("Hello World", message);
  }

  @DisplayName("SpEL 메서드 호출 테스트")
  @Test
  void method_invoke() {

    // given -> 표현식 파싱
    ExpressionParser parser = new SpelExpressionParser();
    Expression sut = parser.parseExpression("'Hello World'.concat('!')");

    // when -> 표현 객체로부터 값을 추출
    String message = (String) sut.getValue();

    // then
    assertEquals("Hello World!", message);
  }

  @DisplayName("SpEL 프로퍼티 접근 테스트")
  @Test
  void access_property() {

    // given -> 표현식 파싱
    ExpressionParser parser = new SpelExpressionParser();
    Expression sut = parser.parseExpression("'Hello World'.bytes.length");

    // when -> 표현 객체로부터 값을 추출
    var length = (Integer) sut.getValue();

    // then
    assertEquals(11, length);
  }

  @DisplayName("SpEL 컨텍스트 루트 객체 사용 테스트")
  @Test
  void using_context() {
    class CustomRoot {

      public String securityWorld() {
        return "Security World";
      }
    }
    // given -> 컨텍스트 루트 객체 설정
    EvaluationContext context = new StandardEvaluationContext(new CustomRoot());
    ExpressionParser parser = new SpelExpressionParser();

    // when -> 표현식 파싱
    Expression sut = parser.parseExpression("securityWorld()");
    String message = sut.getValue(context, String.class);

    // then
    assertEquals("Security World", message);
  }


}
