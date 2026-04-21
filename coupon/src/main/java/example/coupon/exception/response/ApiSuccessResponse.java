package example.coupon.exception.response;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;
import lombok.Builder;

@Builder(access = PRIVATE)
public record ApiSuccessResponse<T>(boolean success, T data) {

  public static <T> ApiSuccessResponse<T> success(T data) {
    return new ApiSuccessResponse.ApiSuccessResponseBuilder<T>()
        .success(true)
        .data(data).build();
  }

  public static <T> ApiSuccessResponse<Map<String, T>> success(String key, T data) {
    return new ApiSuccessResponse.ApiSuccessResponseBuilder<Map<String, T>>()
        .success(true)
        .data(Map.of(key, data)).build();
  }
}
