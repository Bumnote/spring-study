package com.laboratory.security.aop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.laboratory.security.TestUserContext;
import com.laboratory.security.domain.model.Employee;
import com.laboratory.security.repository.ManagementRepository;
import com.laboratory.security.service.V2ManagementService;
import java.util.ArrayList;
import java.util.List;
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
class PreFilterTests extends TestUserContext {

  @SpyBean
  ManagementRepository repository;

  @Autowired
  V2ManagementService sut;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    repository.init();
  }

  @DisplayName("@PreFilter - 직원 본인이 자신의 직원들 추가 성공")
  @Test
  void junhyunny_addEmployees_success() {

    // given
    SecurityContextHolder.setContext(new SecurityContextImpl(junhyunnyAuthentication));

    // when
    sut.addEmployees(
        new ArrayList<>(
            List.of(
                new Employee("M0003", "S0001", "david"),
                new Employee("E0003", "M0001", "smith")
            )
        )
    );

    // then
    var result = repository.getAllEmployees();
    assertTrue(result.containsValue(new Employee("E0003", "M0001", "smith")));
    assertFalse(result.containsValue(new Employee("M0003", "S0001", "david")));
    verify(repository, times(1)).addEmployees(eq(
        List.of(
            new Employee("E0003", "M0001", "smith")
        )
    ));
  }

  @DisplayName("@PreFilter - 직원의 매니저가 직원들 추가 실패")
  @Test
  void jua_addEmployees_success() {

    // given
    SecurityContextHolder.setContext(new SecurityContextImpl(juaAuthentication));

    // then
    assertThrows(
        AuthorizationDeniedException.class,
        // when
        () -> sut.addEmployees(
            new ArrayList<>(
                List.of(
                    new Employee("E0003", "E0001", "smith")
                )
            )
        )
    );
    verify(repository, times(0)).addEmployees(any());
  }

}
