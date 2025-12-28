package com.laboratory.security.domain.model;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

public record DemoAuthenticationUser(
    OAuth2User oauth2User,
    UUID id,
    String userName,
    String email
) implements OidcUser {

  /* [2] OidcUser 인스턴스에 포함된 사용자 정보 반환 */
  @Override
  public Map<String, Object> getClaims() {
    if (oauth2User instanceof OidcUser oidcUser) {
      return oidcUser.getClaims();
    }
    return Map.of();
  }

  @Override
  public OidcUserInfo getUserInfo() {
    if (oauth2User instanceof OidcUser oidcUser) {
      return oidcUser.getUserInfo();
    }
    return null;
  }

  @Override
  public OidcIdToken getIdToken() {
    if (oauth2User instanceof OidcUser oidcUser) {
      return oidcUser.getIdToken();
    }
    return null;
  }

  /* [3] OAuth2User 인스턴스에 포함된 사용자 정보 반환 */
  @Override
  public Map<String, Object> getAttributes() {
    return oauth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oauth2User.getAuthorities();
  }

  @Override
  public String getName() {
    return oauth2User.getName();
  }
}