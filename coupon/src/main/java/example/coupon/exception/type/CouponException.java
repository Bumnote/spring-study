package example.coupon.exception.type;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CouponException extends RuntimeException {

  private final HttpStatus status;
  private final ErrorCode errorCode;

  protected CouponException(HttpStatus status, ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.status = status;
    this.errorCode = errorCode;
  }
}
