package com.laboratory.security.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.laboratory.security.TestUserContext;
import com.laboratory.security.domain.model.Employee;
import com.laboratory.security.repository.ManagementRepository;
import com.laboratory.security.service.V2ManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@SpringBootTest
class PostFilterTests extends TestUserContext {

  @SpyBean
  ManagementRepository repository;

  @Autowired
  V2ManagementService sut;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    repository.init();
  }

  @DisplayName("@PostFilter - 직원 본인이 자신의 직원들 조회 성공")
  @Test
  @Disabled
  void junhyunny_getEmployees_success() {

    // given
    SecurityContextHolder.setContext(new SecurityContextImpl(junhyunnyAuthentication));

    // when
    var result = sut.getMyEmployees();

    // then
    assertEquals(3, result.size());
    assertEquals(new Employee("M0001", "S0001", "junhyunny"), result.get("M0001"));
    assertEquals(new Employee("E0001", "M0001", "jua"), result.get("E0001"));
    verify(repository, times(1)).getAllEmployees();
  }

}
