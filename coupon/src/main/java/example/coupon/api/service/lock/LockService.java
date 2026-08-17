package example.coupon.api.service.lock;

import example.coupon.api.service.coupon.CouponService;
import example.coupon.api.service.coupon.response.CouponIssueResponse;
import example.coupon.common.lock.LockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {

  public static final String COUPON_ISSUE_LOCK_PREFIX = "coupon_issue_lock:";
  private static final int LOCK_TIMEOUT_SECONDS = 3;

  private final LockRepository lockRepository;
  private final CouponService couponService;

  public CouponIssueResponse issueCouponWithLock(Long couponId, Long userId) {
    String key = COUPON_ISSUE_LOCK_PREFIX + couponId;
    return lockRepository.executeWithLock(key, LOCK_TIMEOUT_SECONDS, () -> couponService.issueCoupon(couponId, userId)
    );
  }
}
