package example.coupon.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
public class UserController {

  @GetMapping("/users/{userId}")
  public String getCoupons(@PathVariable Long userId) {
    return "쿠폰 목록";
  }

  @GetMapping("/users/{userId}/usable")
  public String getAvailableCoupons(@PathVariable Long userId) {
    return "사용 가능한 쿠폰 목록";
  }

  @PostMapping("/{issuedCouponId}/use")
  public String useCoupon(@PathVariable Long issuedCouponId) {
    return "쿠폰 사용 완료";
  }
}
