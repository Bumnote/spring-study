package example.coupon.exception.type;

import static example.coupon.exception.type.ErrorCode.COUPON_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CouponNotFoundException extends CouponException {

  public CouponNotFoundException() {
    super(NOT_FOUND, COUPON_NOT_FOUND);
  }
}
