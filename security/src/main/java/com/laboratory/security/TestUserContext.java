package com.laboratory.security;

import java.util.List;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public abstract class TestUserContext {

  /* [1] 관리자 사용자 */
  protected Authentication supervisorAuthentication = new
      TestingAuthenticationToken(
      "S0001",
      "123",
      List.of(
          new SimpleGrantedAuthority("ROLE_SUPERVISOR")
      )
  );

  /* [2] junhyunny 사용자 */
  protected Authentication junhyunnyAuthentication = new
      TestingAuthenticationToken(
      "M0001",
      "123",
      List.of(
          new SimpleGrantedAuthority("ROLE_MANAGER")
      )
  );

  /* [3] tangerine 사용자 */
  protected Authentication tangerineAuthentication = new
      TestingAuthenticationToken(
      "M0002",
      "123",
      List.of(
          new SimpleGrantedAuthority("ROLE_MANAGER")
      )
  );

  /* [4] jua 사용자 */
  protected Authentication juaAuthentication = new TestingAuthenticationToken(
      "E0001",
      "123",
      List.of(
          new SimpleGrantedAuthority("ROLE_EMPLOYEE")
      )
  );
  /* [5] tory 사용자 */
  protected Authentication toryAuthentication = new TestingAuthenticationToken(
      "E0002",
      "123",
      List.of(
          new SimpleGrantedAuthority("ROLE_EMPLOYEE")
      )
  );

}
