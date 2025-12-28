package com.laboratory.security.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.laboratory.security.TestUserContext;
import com.laboratory.security.repository.ManagementRepository;
import com.laboratory.security.service.V2ManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@SpringBootTest
class PostAuthorizeTests extends TestUserContext {

  @SpyBean
  ManagementRepository repository;

  @Autowired
  V2ManagementService sut;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    repository.init();
  }

  @DisplayName("@PostAuthorize - 직원 본인이 자신의 정보 조회 성공")
  @Test
  void junhyunny_getEmployeeByIdForSelf_success() {

    // given
    SecurityContextHolder.setContext(new SecurityContextImpl(junhyunnyAuthentication));

    // when
    var result = sut.getEmployeeById("M0001");

    // then
    assertEquals("M0001", result.id());
    assertEquals("S0001", result.managerId());
    assertEquals("junhyunny", result.name());
    verify(repository, times(1)).getEmployeeById(any());
  }

  @DisplayName("@PostAuthorize - 직원의 매니저가 직원 정보 조회 성공")
  @Test
  void junhyunny_getEmployeeByIdForJua_success() {

    // given
    SecurityContextHolder.setContext(new SecurityContextImpl(junhyunnyAuthentication));

    // when
    var result = sut.getEmployeeById("E0001");

    // then
    assertEquals("E0001", result.id());
    assertEquals("M0001", result.managerId());
    assertEquals("jua", result.name());
    verify(repository, times(1)).getEmployeeById(any());
  }

  @DisplayName("@PostAuthorize - 직원의 매니저가 아닌 사용자가 직원 정보 조회 실패")
  @Test
  void junhyunny_getEmployeeByIdForTory_throwAuthorizationDeniedException() {

    // given
    SecurityContextHolder.setContext(new SecurityContextImpl(junhyunnyAuthentication));

    // then
    assertThrows(
        // when
        AuthorizationDeniedException.class,
        () -> sut.getEmployeeById("E0002")
    );
    verify(repository, times(1)).getEmployeeById(any());
  }
}

