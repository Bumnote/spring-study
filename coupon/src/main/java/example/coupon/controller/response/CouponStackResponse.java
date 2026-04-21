package example.coupon.controller.response;

import example.coupon.entity.coupon.Coupon;

public record CouponStackResponse(
    Long couponId,
    Integer totalQuantity,
    Integer issuedQuantity,
    Integer remainingQuantity
) {

  public static CouponStackResponse from(Coupon coupon) {
    return new CouponStackResponse(
        coupon.getId(),
        coupon.getTotalQuantity(),
        coupon.getIssuedQuantity(),
        coupon.getTotalQuantity() - coupon.getIssuedQuantity()
    );
  }

}
