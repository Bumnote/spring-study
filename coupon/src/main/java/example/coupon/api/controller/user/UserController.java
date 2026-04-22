package example.coupon.api.controller.user;

import example.coupon.api.service.coupon.request.CouponRequest;
import example.coupon.api.service.user.response.UserCouponResponse;
import example.coupon.api.service.user.response.UserCouponUsableResponse;
import example.coupon.api.service.user.response.UserCouponUseResponse;
import example.coupon.common.exception.response.ApiSuccessResponse;
import example.coupon.api.service.user.UserCouponService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class UserController {

  private final UserCouponService userCouponService;

  @GetMapping("/users/{userId}")
  public ApiSuccessResponse<List<UserCouponResponse>> getCoupons(@PathVariable Long userId) {
    List<UserCouponResponse> result = userCouponService.getUserCoupons(userId);
    return ApiSuccessResponse.success(result);
  }

  @GetMapping("/users/{userId}/usable")
  public ApiSuccessResponse<List<UserCouponUsableResponse>> getAvailableCoupons(@PathVariable Long userId) {
    List<UserCouponUsableResponse> result = userCouponService.getUserUsableCoupons(userId);
    return ApiSuccessResponse.success(result);
  }

  @PostMapping("/{issuedCouponId}/use")
  public ApiSuccessResponse<UserCouponUseResponse> useCoupon(
      @PathVariable Long issuedCouponId,
      @RequestBody CouponRequest request
  ) {
    UserCouponUseResponse result = userCouponService.useCoupon(issuedCouponId, request);
    return ApiSuccessResponse.success(result);
  }
}
