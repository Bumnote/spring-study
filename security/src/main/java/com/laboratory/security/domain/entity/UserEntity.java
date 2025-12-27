package com.laboratory.security.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class UserEntity {

  @Id
  private String id;
  private String password;
  private String role;
  private boolean accountExpired;
  private boolean accountLocked;
  private boolean credentialExpired;
  private boolean enabled;

  public UserEntity(String username, String password) {
    this.id = username;
    this.password = password;
    this.role = "ROLE_USER";
    this.accountExpired = false;
    this.accountLocked = false;
    this.credentialExpired = false;
    this.enabled = true;
  }
}