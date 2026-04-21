package example.coupon.controller.response;

import example.coupon.entity.Status;
import example.coupon.entity.issuedCoupon.IssuedCoupon;
import java.time.LocalDateTime;

public record CouponIssueResponse(
    Long issueCouponId,
    Long couponId,
    String couponName,
    Status status,
    LocalDateTime issuedAt,
    LocalDateTime expiredAt
) {

  public static CouponIssueResponse from(IssuedCoupon issuedCoupon) {
    return new CouponIssueResponse(
        issuedCoupon.getId(),
        issuedCoupon.getCoupon().getId(),
        issuedCoupon.getCoupon().getName(),
        issuedCoupon.getStatus(),
        issuedCoupon.getIssuedAt(),
        issuedCoupon.getExpiredAt()
    );
  }

}
