package example.coupon.api.service.coupon.response;

import example.coupon.common.entity.CouponType;
import example.coupon.domain.coupon.entity.Coupon;
import java.time.LocalDateTime;

public record CouponResponse(
    Long couponId,
    String name,
    CouponType couponType,
    Integer totalQuantity,
    Integer remainingQuantity,
    LocalDateTime startDate,
    LocalDateTime endDate
) {

  public static CouponResponse from(Coupon coupon) {
    return new CouponResponse(
        coupon.getId(),
        coupon.getName(),
        coupon.getCouponType(),
        coupon.getTotalQuantity(),
        coupon.getTotalQuantity() - coupon.getIssuedQuantity(),
        coupon.getStartDate(),
        coupon.getEndDate()
    );
  }

}
