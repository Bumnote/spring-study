package example.coupon.exception.type;

import static example.coupon.exception.type.ErrorCode.COUPON_ALREADY_ISSUED;
import static org.springframework.http.HttpStatus.CONFLICT;

public class DuplicateCouponIssueException extends CouponException {

  public DuplicateCouponIssueException() {
    super(CONFLICT, COUPON_ALREADY_ISSUED);
  }
}
