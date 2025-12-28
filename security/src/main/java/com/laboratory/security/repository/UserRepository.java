package com.laboratory.security.repository;

import com.laboratory.security.domain.entity.UserEntity;
import com.laboratory.security.service.LoginType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  Optional<UserEntity> findBySubjectAndLoginType(String subject, LoginType loginType);
}