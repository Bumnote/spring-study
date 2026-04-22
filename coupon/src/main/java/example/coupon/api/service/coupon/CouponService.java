package example.coupon.api.service.coupon;

import static example.coupon.common.entity.Status.ACTIVE;

import example.coupon.api.service.coupon.response.CouponIssueResponse;
import example.coupon.domain.coupon.entity.Coupon;
import example.coupon.domain.coupon.entity.IssuedCoupon;
import example.coupon.common.exception.type.CouponNotFoundException;
import example.coupon.common.exception.type.DuplicateCouponIssueException;
import example.coupon.domain.coupon.repository.CouponRepository;
import example.coupon.domain.coupon.repository.IssuedCouponRepository;
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
