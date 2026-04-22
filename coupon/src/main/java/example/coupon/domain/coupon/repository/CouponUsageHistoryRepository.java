package example.coupon.domain.coupon.repository;

import example.coupon.domain.coupon.entity.CouponUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageHistoryRepository extends JpaRepository<CouponUsageHistory, Long> {

}
