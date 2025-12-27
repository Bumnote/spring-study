package com.laboratory.security.controller;

import static org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

  private final static String DEFAULT_ERROR_QUERY_PARAM = "fail";
  private final static String DEFAULT_ERROR_MESSAGE = "사용자 정보가 유효하지 않습니다.";

  @GetMapping("/login")
  public ModelAndView login(HttpServletRequest servletRequest) {
    var modelAndView = new ModelAndView("login");
    modelAndView.addObject("errorMessage", errorMessage(servletRequest));
    return modelAndView;
  }

  private String errorMessage(HttpServletRequest servletRequest) {
    var session = servletRequest.getSession(false);
    var isFailed = servletRequest.getParameter(DEFAULT_ERROR_QUERY_PARAM) != null;

    /* [1] 에러가 아닌 경우 */
    if (!isFailed) {
      return null;
    }

    /* [2] 기본 예외 메시지 반환 */
    if (session == null || !(session.getAttribute(AUTHENTICATION_EXCEPTION) instanceof AuthenticationException exception)
    ) {
      return DEFAULT_ERROR_MESSAGE;
    }
    /* [3] 예외 객체에 에러 메시지가 있는 경우 이를 사용 */
    var errorMessage = exception.getMessage();
    return StringUtils.hasText(errorMessage) ? errorMessage : DEFAULT_ERROR_MESSAGE;
  }
}
