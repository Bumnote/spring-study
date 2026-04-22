package example.coupon.service;

import static example.coupon.entity.Status.ACTIVE;

import example.coupon.controller.request.CouponRequest;
import example.coupon.controller.response.UserCouponResponse;
import example.coupon.controller.response.UserCouponUsableResponse;
import example.coupon.controller.response.UserCouponUseResponse;
import example.coupon.entity.issuedCoupon.IssuedCoupon;
import example.coupon.repository.issuedCoupon.IssuedCouponRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCouponService {

  private final IssuedCouponRepository issuedCouponRepository;
  private final ClientHttpRequestFactorySettings clientHttpRequestFactorySettings;

  public List<UserCouponResponse> getUserCoupons(Long userId) {
    return issuedCouponRepository.findByUserId(userId).stream()
        .map(UserCouponResponse::from)
        .toList();
  }

  public List<UserCouponUsableResponse> getUserUsableCoupons(Long userId) {
    return issuedCouponRepository.findByUserIdAndStatus(userId, ACTIVE).stream()
        .map(UserCouponUsableResponse::from)
        .toList();
  }

  @Transactional
  public UserCouponUseResponse useCoupon(Long issuedCouponId, CouponRequest request) {
    IssuedCoupon usableIssuedCoupon = issuedCouponRepository.findByIdAndUserIdAndStatus(issuedCouponId, request.userId(), ACTIVE);
    UserCouponUseResponse userCouponUseResponse = UserCouponUseResponse.from(usableIssuedCoupon, request);
    usableIssuedCoupon.use();
    return userCouponUseResponse;
  }
}
