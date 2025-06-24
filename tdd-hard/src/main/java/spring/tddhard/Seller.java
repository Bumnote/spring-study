package spring.tddhard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Seller {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long dataKey;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String username;

  @Column(length = 1_000)
  private String hashedPassword;

}
