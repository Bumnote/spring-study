package com.laboratory.security.service;

import com.laboratory.security.domain.entity.UserEntity;
import com.laboratory.security.domain.model.DemoAuthenticationUser;
import com.laboratory.security.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleOidcUserService extends OidcUserService {

  private final UserRepository userRepository;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

    OidcUser oidcUser = super.loadUser(userRequest);
    Optional<UserEntity> optionalUser = userRepository.findBySubjectAndLoginType(
        oidcUser.getSubject(),
        LoginType.GOOGLE
    );

    UserEntity userEntity = optionalUser
        .orElseGet(() -> userRepository.save(
            new UserEntity(
                UUID.randomUUID(),
                oidcUser.getSubject(),
                LoginType.GOOGLE
            )
        ));

    return new DemoAuthenticationUser(
        oidcUser,
        userEntity.getId(),
        oidcUser.getAttribute("name"),
        oidcUser.getAttribute("email")
    );
  }
}
