package com.laboratory.security.controller;

import com.laboratory.security.domain.model.SignUp;
import com.laboratory.security.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SignUpController {

  private final SignUpService signUpService;

  @GetMapping("/signup")
  public String signup() {
    return "signup";
  }

  @PostMapping("/signup")
  public String signup(SignUp requestBody) {
    signUpService.signUp(requestBody);
    return "redirect:/login";
  }
}