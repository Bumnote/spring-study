package example.coupon.service;

import static example.coupon.entity.Status.ACTIVE;

import example.coupon.controller.response.CouponIssueResponse;
import example.coupon.entity.coupon.Coupon;
import example.coupon.entity.issuedCoupon.IssuedCoupon;
import example.coupon.exception.type.CouponNotFoundException;
import example.coupon.exception.type.DuplicateCouponIssueException;
import example.coupon.repository.coupon.CouponRepository;
import example.coupon.repository.issuedCoupon.IssuedCouponRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;
  private final IssuedCouponRepository issuedCouponRepository;

  @Transactional
  public CouponIssueResponse issueCoupon(Long couponId, Long userId) {
    Coupon coupon = couponRepository.findById(couponId, ACTIVE).orElseThrow(CouponNotFoundException::new);

    if (issuedCouponRepository.existsByCouponIdAndUserId(couponId, userId)) {
      throw new DuplicateCouponIssueException();
    }

    coupon.issue();

    IssuedCoupon issuedCoupon = IssuedCoupon.builder()
        .coupon(coupon)
        .userId(userId)
        .status(ACTIVE)
        .issuedAt(LocalDateTime.now())
        .expiredAt(coupon.getEndDate())
        .build();

    IssuedCoupon savedIssuedCoupon = issuedCouponRepository.save(issuedCoupon);
    return CouponIssueResponse.from(savedIssuedCoupon);
  }
}
