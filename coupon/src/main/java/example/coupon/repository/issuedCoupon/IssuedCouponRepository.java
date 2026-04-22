package example.coupon.repository.issuedCoupon;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import example.coupon.entity.Status;
import example.coupon.entity.issuedCoupon.IssuedCoupon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {

  boolean existsByCouponIdAndUserId(Long couponId, Long userId);

  List<IssuedCoupon> findByUserId(Long userId);

  List<IssuedCoupon> findByUserIdAndStatus(Long userId, Status status);

  @Lock(PESSIMISTIC_WRITE)
  IssuedCoupon findByIdAndUserIdAndStatus(Long id, Long userId, Status status);
}
