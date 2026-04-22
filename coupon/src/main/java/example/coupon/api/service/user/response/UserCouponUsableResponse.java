package example.coupon.api.service.user.response;

import example.coupon.common.entity.CouponType;
import example.coupon.domain.coupon.entity.IssuedCoupon;
import java.time.LocalDateTime;

public record UserCouponUsableResponse(
    Integer issuedCouponId,
    String couponName,
    CouponType couponType,
    Integer discountValue,
    Integer minOrderAmount,
    Integer maxDiscountAmount,
    LocalDateTime expiredAt
) {

  public static UserCouponUsableResponse from(IssuedCoupon issuedCoupon) {
    return new UserCouponUsableResponse(
        issuedCoupon.getId().intValue(),
        issuedCoupon.getCoupon().getName(),
        issuedCoupon.getCoupon().getCouponType(),
        issuedCoupon.getCoupon().getDiscountValue(),
        issuedCoupon.getCoupon().getMinOrderAmount(),
        issuedCoupon.getCoupon().getMaxDiscountAmount(),
        issuedCoupon.getExpiredAt()
    );
  }
}
