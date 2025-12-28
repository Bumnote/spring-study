package com.laboratory.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class MainController {

  private final InMemoryUserDetailsManager userManager;

  @GetMapping
  public ModelAndView index(
      @AuthenticationPrincipal UserDetails principal
  ) {
    /* [1] 인증된 사용자 정보 조회 */
    var user = userManager.loadUserByUsername(
        principal.getUsername()
    );
    /* [2] HTML 문서 이름을 모델-앤-뷰 객체에 지정 */
    var mav = new ModelAndView("index");
    /* [3] 페이지에 필요한 사용자 이름 추가 */
    mav.addObject("username", user.getUsername());
    /* [4] 페이지에 필요한 사용자 역할 추가 */
    mav.addObject(
        "role",
        user.getAuthorities()
            .stream()
            .findFirst()
            .orElseThrow()
            .getAuthority()
    );
    return mav;
  }

}
