package example.coupon.entity.history;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import example.coupon.entity.BaseEntity;
import example.coupon.entity.issuedCoupon.IssuedCoupon;
import jakarta.persistence.Entity;
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
public class CouponUsageHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "issued_coupon_id")
  private IssuedCoupon issuedCoupon;
  private Long userId;
  private Long orderId;
  private Integer discountAmount;
  private LocalDateTime usedAt;

  @Builder
  public CouponUsageHistory(IssuedCoupon issuedCoupon, Long userId, Long orderId,
      Integer discountAmount, LocalDateTime usedAt) {
    this.issuedCoupon = issuedCoupon;
    this.userId = userId;
    this.orderId = orderId;
    this.discountAmount = discountAmount;
    this.usedAt = usedAt;
  }

}
