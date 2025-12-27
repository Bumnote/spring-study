package com.laboratory.security.domain.model;

import com.laboratory.security.domain.entity.UserEntity;
import java.util.function.UnaryOperator;

public record SignUp(
    String username,
    String password
) {

  public UserEntity toEntity(UnaryOperator<String> encode) {
    return new UserEntity(username, encode.apply(password));
  }
}