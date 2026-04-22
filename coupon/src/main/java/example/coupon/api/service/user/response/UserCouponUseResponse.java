package example.coupon.api.service.user.response;

import example.coupon.api.service.coupon.request.CouponRequest;
import example.coupon.domain.coupon.entity.IssuedCoupon;
import java.time.LocalDateTime;

public record UserCouponUseResponse(
    Integer issuedCouponId,
    Long orderId,
    Integer discountAmount,
    LocalDateTime usedAt
) {

  public static UserCouponUseResponse from(IssuedCoupon usableIssuedCoupon, CouponRequest couponInfo) {
    return new UserCouponUseResponse(
        usableIssuedCoupon.getId().intValue(),
        couponInfo.orderId(),
        calculateDiscountAmount(usableIssuedCoupon, couponInfo.orderAmount()),
        LocalDateTime.now()
    );
  }

  private static Integer calculateDiscountAmount(IssuedCoupon usableIssuedCoupon, Integer orderAmount) {
    Integer maxDiscountAmount = usableIssuedCoupon.getCoupon().getMaxDiscountAmount();
    Integer discountValue = usableIssuedCoupon.getCoupon().getDiscountValue();
    int resultDiscountAmount = orderAmount * discountValue / 100;
    return Math.min(resultDiscountAmount, maxDiscountAmount);
  }
}
