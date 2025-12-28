package com.laboratory.security.controller;

import java.util.List;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2")
public class V2UserController {

  /* [1] 사전 인가 처리 수행 */
  @PreAuthorize("hasRole('MANAGER')")
  @GetMapping("/users")
  public List<String> users() {
    return List.of("junhyunny");
  }

  /* [2] 사후 인가 처리 수행 */
  @PostAuthorize("returnObject == authentication.principal.username")
  @GetMapping("/users/me")
  public String me() {
    return "tangerine";
  }

}
