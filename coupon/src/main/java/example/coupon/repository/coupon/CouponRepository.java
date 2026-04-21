package example.coupon.repository.coupon;

import example.coupon.entity.Status;
import example.coupon.entity.coupon.Coupon;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

  List<Coupon> findAll();

  Optional<Coupon> findById(Long id);

  @Query("select c from Coupon c where c.id = :id and c.status = :status")
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Coupon> findById(@Param(value = "id") Long id, @Param("status") Status status);

}
