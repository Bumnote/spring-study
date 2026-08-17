package example.coupon.common.exception.type;

import static example.coupon.common.exception.type.ErrorCode.COUPON_LOCK_ACQUISITION_FAILED;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class CouponLockAcquisitionException extends CouponException {

  public CouponLockAcquisitionException() {
    super(SERVICE_UNAVAILABLE, COUPON_LOCK_ACQUISITION_FAILED);
  }
}
