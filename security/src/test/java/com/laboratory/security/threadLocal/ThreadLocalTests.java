package com.laboratory.security.threadLocal;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class ThreadLocalTests {

  @DisplayName("ThreadLocal 방식의 SecurityContextHolder 테스트")
  @Test
  void security_from_other_thread_is_null() throws InterruptedException {

    // given
    var testingToken = new TestingAuthenticationToken(
        "Junhyunny",
        "12345",
        "ROLE_USER"
    );
    var securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(testingToken);
    SecurityContextHolder.setContext(securityContext);

    // when
    var contextArray = new SecurityContext[1];
    var thread = new Thread(() -> {
      var context = SecurityContextHolder.getContext();
      contextArray[0] = context;
    });
    thread.start();
    thread.join();

    // then
    var result = SecurityContextHolder.getContext();
    assertEquals(securityContext, result);
    assertEquals(testingToken, result.getAuthentication());
    assertNull(contextArray[0].getAuthentication());
  }

  @DisplayName("InheritableThreadLocal 방식의 SecurityContextHolder 테스트")
  @Test
  void security_context_is_same_in_inheritable_thread_local_mode() throws InterruptedException {

    // given
    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    var testingToken = new TestingAuthenticationToken(
        "Junhyunny",
        "12345",
        "ROLE_USER"
    );
    var securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(testingToken);
    SecurityContextHolder.setContext(securityContext);

    // when
    var contextArray = new SecurityContext[1];
    var thread = new Thread(() -> {
      var context = SecurityContextHolder.getContext();
      contextArray[0] = context;
    });
    thread.start();
    thread.join();

    // then
    var result = SecurityContextHolder.getContext();
    assertEquals(securityContext, result);
    assertEquals(testingToken, result.getAuthentication());
    assertEquals(securityContext, contextArray[0]);
    assertEquals(testingToken, contextArray[0].getAuthentication());
  }

  @DisplayName("InheritableThreadLocal 방식의 SecurityContextHolder와 CompletableFuture 테스트")
  @Test
  void security_context_is_null_when_completable_future_in_inheritable_thread_local_mode() {

    // given
    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    var testingToken = new TestingAuthenticationToken(
        "Junhyunny",
        "12345",
        "ROLE_USER"
    );
    var securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(testingToken);
    SecurityContextHolder.setContext(securityContext);

    // when
    var contextArray = new SecurityContext[1];
    var completableFuture = CompletableFuture.runAsync(() -> {
      var context = SecurityContextHolder.getContext();
      contextArray[0] = context;
    });
    completableFuture.join();

    // then
    var result = SecurityContextHolder.getContext();
    assertEquals(securityContext, result);
    assertEquals(testingToken, result.getAuthentication());
    assertNull(contextArray[0].getAuthentication());
  }

  @DisplayName("Global 방식의 SecurityContextHolder 테스트")
  @Test
  void security_context_is_same_from_global() throws InterruptedException {

    // given
    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
    var testingToken = new TestingAuthenticationToken(
        "Junhyunny",
        "12345",
        "ROLE_USER"
    );
    var securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(testingToken);
    SecurityContextHolder.setContext(securityContext);

    // when
    var contextArray = new SecurityContext[2];
    var thread = new Thread(() -> {
      var context = SecurityContextHolder.getContext();
      contextArray[0] = context;
    });
    thread.start();
    thread.join();
    var future = CompletableFuture.runAsync(() -> {
      var context = SecurityContextHolder.getContext();
      contextArray[1] = context;
    });
    future.join();

    // then
    var result = SecurityContextHolder.getContext();
    assertEquals(securityContext, result);
    assertEquals(testingToken, result.getAuthentication());
    assertEquals(securityContext, contextArray[0]);
    assertEquals(testingToken, contextArray[0].getAuthentication());
    assertEquals(securityContext, contextArray[1]);
    assertEquals(testingToken, contextArray[1].getAuthentication());
  }
}
