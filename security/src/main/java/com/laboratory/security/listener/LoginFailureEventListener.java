package com.laboratory.security.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureEventListener {

  private final static Logger logger = LoggerFactory.getLogger(LoginFailureEventListener.class);

  @EventListener
  /* [1] 비밀번호가 틀린 인증 실패에 관련된 AuthenticationFailureBadCredentialsEvent 객체 주입 */
  public void badCredentialListener(AuthenticationFailureBadCredentialsEvent event) {
    var authentication = event.getAuthentication();
    var exception = event.getException();
    /* [3] 로그 출력 */
    logger.warn(
        "send authentication fail message via email for {} ({})",
        authentication.getName(),
        exception.getMessage()
    );
  }

  @EventListener
  /* [2] 만료된 사용자에 관련된 AuthenticationFailureExpiredEvent 객체 주입 */
  public void accessExpiredListener(AuthenticationFailureExpiredEvent event) {
    var authentication = event.getAuthentication();
    var exception = event.getException();
    /* [3] 로그 출력 */
    logger.warn(
        "send authentication fail message via email for {} ({})",
        authentication.getName(),
        exception.getMessage()
    );
  }
}
