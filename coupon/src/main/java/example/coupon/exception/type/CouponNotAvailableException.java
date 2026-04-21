package example.coupon.exception.type;

import static example.coupon.exception.type.ErrorCode.COUPON_NOT_AVAILABLE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class CouponNotAvailableException extends CouponException {

  public CouponNotAvailableException() {
    super(BAD_REQUEST, COUPON_NOT_AVAILABLE);
  }
}
