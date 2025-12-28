package com.laboratory.security.aop;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.laboratory.security.TestUserContext;
import com.laboratory.security.domain.model.Employee;
import com.laboratory.security.service.V1ManagementService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@SpringBootTest
class DenyAllTests extends TestUserContext {

  @Autowired
  V1ManagementService sut;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @DisplayName("@PreAuthorize denyAll() 테스트")
  @Test
  void junhyunny_denyAll() {

    // given & when -> 사전에 준비된 사용자 정보를 사용
    SecurityContextHolder.setContext(new SecurityContextImpl(junhyunnyAuthentication));

    // then
    assertThrows(
        AuthorizationDeniedException.class,
        () -> sut.getMyEmployees()
    );
    assertThrows(
        AuthorizationDeniedException.class,
        () -> sut.getEmployeeById("S0001")
    );
    assertThrows(
        AuthorizationDeniedException.class,
        () -> sut.addEmployee(new Employee("E0003", "M0001", "david"))
    );
    assertThrows(
        AuthorizationDeniedException.class,
        () -> sut.addEmployees(List.of(new Employee("E0003", "M0001",
            "david")))
    );
  }
}
