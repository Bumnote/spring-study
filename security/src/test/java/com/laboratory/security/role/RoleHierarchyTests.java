package com.laboratory.security.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class RoleHierarchyTests {

  @DisplayName("하나의 역할로부터 하위 권한 탐색 테스트")
  @Test
  void one_role() {

    // given -> 역할 계층 설정
    var sut = RoleHierarchyImpl.withRolePrefix("")
        .role("ROLE_ADMIN").implies("ROLE_MANAGER")
        .role("ROLE_MANAGER").implies("ROLE_USER")
        .role("ROLE_USER").implies("ROLE_VISITOR")
        .role("WRITE::HR").implies("READ::HR")
        .build();

    // when -> 하위 권한 탐색
    var result = sut.getReachableGrantedAuthorities(
        List.of(new SimpleGrantedAuthority("ROLE_MANAGER"))
    );

    // then
    assertEquals(result.size(), 3);
    assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_MANAGER")));
    assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_USER")));
    assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_VISITOR")));
  }

  @DisplayName("하나의 역할과 권한으로부터 하위 권한 탐색 테스트")
  @Test
  void one_role_with_authority() {

    // given -> 역할 계층 설정
    var sut = RoleHierarchyImpl.fromHierarchy("""
        ROLE_ADMIN > ROLE_MANAGER > ROLE_USER > ROLE_VISITOR
        WRITE::HR > READ::HR""");

    // when -> 하위 권한 탐색
    var result = sut.getReachableGrantedAuthorities(
        List.of(
            new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("WRITE::HR")
        )
    );

    // then
    assertEquals(result.size(), 6);
    assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_MANAGER")));
    assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_USER")));
    assertTrue(result.contains(new SimpleGrantedAuthority("ROLE_VISITOR")));
    assertTrue(result.contains(new SimpleGrantedAuthority("WRITE::HR")));
    assertTrue(result.contains(new SimpleGrantedAuthority("READ::HR")));
  }
}
