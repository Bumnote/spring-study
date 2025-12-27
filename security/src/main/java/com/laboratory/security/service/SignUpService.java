package com.laboratory.security.service;

import com.laboratory.security.domain.model.SignUp;
import com.laboratory.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void signUp(SignUp requestBody) {
    var user = requestBody.toEntity(passwordEncoder::encode);
    userRepository.save(user);
  }
}