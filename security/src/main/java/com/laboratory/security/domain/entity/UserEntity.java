package com.laboratory.security.domain.entity;

import com.laboratory.security.service.LoginType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

  @Id
  private UUID id;
  private String subject;
  @Enumerated(value = EnumType.STRING)
  private LoginType loginType;

}