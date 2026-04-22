package example.coupon.common.exception.type;

import static example.coupon.common.exception.type.ErrorCode.COUPON_ALL_ISSUED;
import static org.springframework.http.HttpStatus.CONFLICT;

public class CouponExhaustedException extends CouponException {

  public CouponExhaustedException() {
    super(CONFLICT, COUPON_ALL_ISSUED);
  }
}
