package com.laboratory.security.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationEventListener<T> {

  @EventListener
  public void onFailure(AuthorizationDeniedEvent<T> event) {
    var authentication = event.getAuthentication().get();
    var principal = authentication.getPrincipal();
    System.out.printf("Principal - %s\n\n", principal);
    System.out.printf("Object - %s\n\n", event.getObject());
    System.out.printf("AuthenticationDecision - %s\n\n", event.getAuthorizationDecision());
  }
}
