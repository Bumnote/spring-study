package com.laboratory.security.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

  @GetMapping("/home")
  public ModelAndView home(@AuthenticationPrincipal User user) {
    var modelAndView = new ModelAndView("home");
    modelAndView.addObject("username", user.getUsername());
    return modelAndView;
  }

}