package com.laboratory.security.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessEventListener {

  private final static Logger logger = LoggerFactory.getLogger(LoginSuccessEventListener.class);

  @EventListener
  public void loginSuccessListener(AuthenticationSuccessEvent event) {
    var authentication = event.getAuthentication();

    logger.info(
        "send login success via email for {}",
        authentication.getName()
    );
  }
}