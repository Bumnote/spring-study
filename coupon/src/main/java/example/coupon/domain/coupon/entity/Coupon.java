package example.coupon.domain.coupon.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import example.coupon.common.entity.BaseEntity;
import example.coupon.common.entity.CouponType;
import example.coupon.common.entity.Status;
import example.coupon.common.exception.type.CouponExhaustedException;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class Coupon extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  private String name;
  private String description;
  @Enumerated(EnumType.STRING)
  private CouponType couponType;
  @Enumerated(EnumType.STRING)
  private Status status;
  private Integer discountValue;
  private Integer minOrderAmount;
  private Integer maxDiscountAmount;
  private Integer totalQuantity;
  private Integer issuedQuantity;
  private Integer validDays;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  public void issue() {
    if (issuedQuantity >= totalQuantity) {
      throw new CouponExhaustedException();
    }
    issuedQuantity++;
  }
}
