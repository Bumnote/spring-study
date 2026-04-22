package example.coupon.api.service.coupon.response;

import example.coupon.domain.coupon.entity.Coupon;

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
