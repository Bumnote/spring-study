package com.laboratory.security.domain.model;

import com.laboratory.security.domain.entity.UserEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@AllArgsConstructor
public class User implements UserDetails {

  private final String username;
  private final String password;
  private final List<GrantedAuthority> authorities;
  private boolean accountExpired;
  private boolean accountLocked;
  private boolean credentialExpired;
  private boolean enabled;

  public static UserDetails of(UserEntity user) {
    var roles = new ArrayList<GrantedAuthority>();
    roles.add(new SimpleGrantedAuthority(user.getRole()));
    return User.builder()
        .username(user.getId())
        .password(user.getPassword())
        .authorities(roles)
        .accountExpired(user.isAccountExpired())
        .accountLocked(user.isAccountLocked())
        .credentialExpired(user.isCredentialExpired())
        .enabled(user.isEnabled())
        .build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return !accountExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !accountLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return !credentialExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}