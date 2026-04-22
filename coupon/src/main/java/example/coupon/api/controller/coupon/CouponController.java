package example.coupon.api.controller.coupon;

import example.coupon.api.service.user.request.UserRequest;
import example.coupon.api.service.coupon.response.CouponIssueResponse;
import example.coupon.api.service.coupon.response.CouponResponse;
import example.coupon.api.service.coupon.response.CouponStackResponse;
import example.coupon.common.exception.response.ApiSuccessResponse;
import example.coupon.domain.coupon.repository.CouponRepository;
import example.coupon.api.service.coupon.CouponService;
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
public class CouponController {

  private final CouponService couponService;
  private final CouponRepository couponRepository;

  @GetMapping("/available")
  public ApiSuccessResponse<List<CouponResponse>> getAvailableCoupons() {

    List<CouponResponse> results = couponRepository.findAll().stream()
        .map(CouponResponse::from)
        .toList();

    return ApiSuccessResponse.success(results);
  }

  @GetMapping("/{couponId}/stock")
  public ApiSuccessResponse<CouponStackResponse> getCouponStockInfo(@PathVariable Long couponId) {
    log.info("couponId: {}", couponId);
    CouponStackResponse result = CouponStackResponse.from(couponRepository.findById(couponId).orElse(null));
    return ApiSuccessResponse.success(result);
  }

  @PostMapping("/{couponId}/issue")
  public ApiSuccessResponse<CouponIssueResponse> issueCoupon(@PathVariable Long couponId, @RequestBody UserRequest request) {
    CouponIssueResponse couponIssueResponse = couponService.issueCoupon(couponId, request.userId());
    return ApiSuccessResponse.success(couponIssueResponse);
  }

}
