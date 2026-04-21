package example.coupon.repository.issuedCoupon;

import example.coupon.entity.issuedCoupon.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {

  boolean existsByCouponIdAndUserId(Long couponId, Long userId);

}
