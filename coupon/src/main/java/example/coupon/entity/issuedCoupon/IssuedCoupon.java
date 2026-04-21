package example.coupon.entity.issuedCoupon;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import example.coupon.entity.BaseEntity;
import example.coupon.entity.Status;
import example.coupon.entity.coupon.Coupon;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class IssuedCoupon extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "coupon_id")
  private Coupon coupon;

  private Long userId;
  @Enumerated(EnumType.STRING)
  private Status status;
  private LocalDateTime issuedAt;
  private LocalDateTime expiredAt;
  private LocalDateTime usedAt;

  @Builder
  public IssuedCoupon(Coupon coupon, Long userId, Status status, LocalDateTime issuedAt,
      LocalDateTime expiredAt) {
    this.coupon = coupon;
    this.userId = userId;
    this.status = status;
    this.issuedAt = issuedAt;
    this.expiredAt = expiredAt;
  }

}
