package example.coupon.api.service.user.response;

import example.coupon.common.entity.CouponType;
import example.coupon.common.entity.Status;
import example.coupon.domain.coupon.entity.IssuedCoupon;
import java.time.LocalDateTime;

public record UserCouponResponse(
    Integer issuedCouponId,
    String couponName,
    CouponType couponType,
    Integer discountValue,
    Status status,
    LocalDateTime issuedAt,
    LocalDateTime expiredAt
) {

  public static UserCouponResponse from(IssuedCoupon issuedCoupon) {
    return new UserCouponResponse(
        issuedCoupon.getId().intValue(),
        issuedCoupon.getCoupon().getName(),
        issuedCoupon.getCoupon().getCouponType(),
        issuedCoupon.getCoupon().getDiscountValue(),
        issuedCoupon.getStatus(),
        issuedCoupon.getIssuedAt(),
        issuedCoupon.getExpiredAt()
    );
  }
}
