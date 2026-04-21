package example.coupon.exception.response;

import static lombok.AccessLevel.PRIVATE;

import example.coupon.exception.type.ErrorCode;
import lombok.Builder;

@Builder(access = PRIVATE)
public record ApiErrorResponse<T>(boolean success, T message) {

  public static ApiErrorResponse<String> error(ErrorCode errorCode) {
    return new ApiErrorResponse.ApiErrorResponseBuilder<String>()
        .success(false)
        .message(errorCode.getMessage()).build();
  }
}
