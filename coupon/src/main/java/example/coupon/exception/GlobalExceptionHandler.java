package example.coupon.exception;

import example.coupon.exception.response.ApiErrorResponse;
import example.coupon.exception.type.CouponException;
import example.coupon.exception.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CouponException.class)
  protected ResponseEntity<ApiErrorResponse<String>> handleException(CouponException e) {
    ErrorCode errorCode = e.getErrorCode();
    log.warn("CouponException: {}", errorCode, e);
    return ResponseEntity.status(e.getStatus()).body(ApiErrorResponse.error(errorCode));
  }
}
