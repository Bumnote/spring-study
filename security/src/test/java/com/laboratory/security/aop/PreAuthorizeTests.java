package com.laboratory.security.aop;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.laboratory.security.TestUserContext;
import com.laboratory.security.domain.model.Employee;
import com.laboratory.security.repository.ManagementRepository;
import com.laboratory.security.service.V2ManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@SpringBootTest
class PreAuthorizeTests extends TestUserContext {

  /* [1] 스파이 테스트 더블 준비 */
  @SpyBean
  ManagementRepository repository;
  /* [2] 테스트 대상 주입 */
  @Autowired
  V2ManagementService sut;

  /* [3] 테스트 실행 이전에 상태 초기화 */
  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    repository.init();
  }

  @DisplayName("@PreAuthorize - 관리자(supervisor) 권한으로 자신의 직원 추가 성공")
  @Test
  void superVisorMyEmployee_addEmployee_success() {

    // given
    var newEmployee = new Employee("M0003", "S0001", "david");
    SecurityContextHolder.setContext(new SecurityContextImpl(supervisorAuthentication));

    // when
    sut.addEmployee(newEmployee);
    var result = repository.getAllEmployees();

    // then
    assertTrue(result.containsValue(newEmployee));
    verify(repository, times(1)).addEmployee(any());
  }

  @DisplayName("@PreAuthorize - 관리자(supervisor) 권한으로 자신의 직원 추가 성공 v2")
  @Test
  void superVisorMyEmployee_addEmployee_success_v2() {

    // given
    var newEmployee = new Employee("M0003", "S0001", "david");
    SecurityContextHolder.setContext(new SecurityContextImpl(supervisorAuthentication));

    // when
    sut.addEmployee(newEmployee);
    var result = repository.getAllEmployees();

    // then
    assertTrue(result.containsValue(newEmployee));
    verify(repository, times(1)).addEmployee(any());
  }

}
