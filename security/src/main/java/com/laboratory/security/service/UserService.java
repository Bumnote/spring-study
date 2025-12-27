package com.laboratory.security.service;

import com.laboratory.security.domain.model.User;
import com.laboratory.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    var user = userRepository.findById(username)
        .orElseThrow(
            () -> new UsernameNotFoundException(
                String.format("%s is not existed", username)
            )
        );

    return User.of(user);
  }
}