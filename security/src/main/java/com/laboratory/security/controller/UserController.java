package com.laboratory.security.controller;

import com.laboratory.security.domain.RoleCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final InMemoryUserDetailsManager userManager;

  @PostMapping
  public String updateUserRole(
      @AuthenticationPrincipal UserDetails principal,
      RoleCommand command
  ) {

    /* [1] 현재 로그인 사용자 정보 조회 */
    var user = userManager.loadUserByUsername(
        principal.getUsername()
    );
    /* [2] 역할 변경 후 업데이트 */
    userManager.updateUser(
        User.withUserDetails(user)
            .authorities(command.role())
            .build()
    );
    /* [3] 사용자 화면을 메인 화면으로 리다이렉트 */
    return "redirect:/";
  }
}
