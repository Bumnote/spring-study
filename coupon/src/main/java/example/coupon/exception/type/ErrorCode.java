package example.coupon.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  COUPON_NOT_FOUND("쿠폰을 찾을 수 없습니다."),
  COUPON_ALL_ISSUED("쿠폰 재고가 소진되었습니다."),
  COUPON_ALREADY_ISSUED("이미 발급받은 쿠폰입니다."),
  COUPON_NOT_AVAILABLE("쿠폰의 발급 기간이 아닙니다.");

  private final String message;
}
