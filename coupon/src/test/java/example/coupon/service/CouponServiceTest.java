package example.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;

import example.coupon.entity.CouponType;
import example.coupon.entity.Status;
import example.coupon.entity.coupon.Coupon;
import example.coupon.repository.coupon.CouponRepository;
import example.coupon.repository.issuedCoupon.IssuedCouponRepository;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CouponServiceTest {

  @Autowired
  private CouponService couponService;

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  private IssuedCouponRepository issuedCouponRepository;

  private Long couponId;

  @BeforeEach
  void setUp() throws Exception {
    issuedCouponRepository.deleteAllInBatch();
    couponRepository.deleteAllInBatch();

    Coupon coupon = createCoupon(100);
    Coupon saved = couponRepository.save(coupon);
    this.couponId = saved.getId();
    log.info("테스트용 쿠폰 생성 - ID: {}, 총 발급 가능 수량: {}", couponId, coupon.getTotalQuantity());
  }

  @AfterEach
  void tearDown() {
    issuedCouponRepository.deleteAllInBatch();
    couponRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("1,000명이 동시에 요청해도 쿠폰 100개만 정확히 발급된다")
  void issueCoupon_concurrent_1000users() throws InterruptedException {
    int threadCount = 1000;
    int totalQuantity = 100;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch readyLatch = new CountDownLatch(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);
    AtomicInteger successCount = new AtomicInteger();
    AtomicInteger failCount = new AtomicInteger();

    for (int i = 0; i < threadCount; i++) {
      long userId = i + 1L;
      executor.submit(() -> {
        readyLatch.countDown();
        try {
          startLatch.await();
          couponService.issueCoupon(couponId, userId);
          log.info("쿠폰 발급 성공 - userId: {}", userId);
          successCount.incrementAndGet();
        } catch (Exception e) {
          failCount.incrementAndGet();
        } finally {
          doneLatch.countDown();
        }
      });
    }

    readyLatch.await();
    long startNanos = System.nanoTime();
    startLatch.countDown();
    boolean completed = doneLatch.await(60, TimeUnit.SECONDS);
    long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
    executor.shutdown();

    Coupon coupon = couponRepository.findById(couponId).orElseThrow();
    long issuedRecords = issuedCouponRepository.count();
    log.info("동시 발급 테스트 완료 - 소요시간: {}ms, 성공: {}, 실패: {}, 발급수량: {}, 완료여부: {}",
        elapsedMs, successCount.get(), failCount.get(), coupon.getIssuedQuantity(), completed);

    assertThat(successCount.get()).isEqualTo(totalQuantity);
    assertThat(failCount.get()).isEqualTo(threadCount - totalQuantity);
    assertThat(coupon.getIssuedQuantity()).isEqualTo(totalQuantity);
    assertThat(issuedRecords).isEqualTo(totalQuantity);
  }

  @Test
  @DisplayName("동일 사용자가 동시에 100번 요청해도 정확히 1번만 발급된다")
  void issueCoupon_concurrent_sameUser() throws InterruptedException {
    int threadCount = 100;
    long userId = 1L;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch readyLatch = new CountDownLatch(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);
    AtomicInteger successCount = new AtomicInteger();
    AtomicInteger failCount = new AtomicInteger();

    for (int i = 0; i < threadCount; i++) {
      executor.submit(() -> {
        readyLatch.countDown();
        try {
          startLatch.await();
          couponService.issueCoupon(couponId, userId);
          successCount.incrementAndGet();
        } catch (Exception e) {
          failCount.incrementAndGet();
        } finally {
          doneLatch.countDown();
        }
      });
    }

    readyLatch.await();
    startLatch.countDown();
    doneLatch.await(30, TimeUnit.SECONDS);
    executor.shutdown();

    Coupon coupon = couponRepository.findById(couponId).orElseThrow();
    long issuedRecords = issuedCouponRepository.count();

    assertThat(successCount.get()).isEqualTo(1);
    assertThat(failCount.get()).isEqualTo(threadCount - 1);
    assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
    assertThat(issuedRecords).isEqualTo(1);
  }

  private Coupon createCoupon(int totalQuantity) throws Exception {
    Coupon coupon = new Coupon();
    setField(coupon, "name", "테스트 쿠폰");
    setField(coupon, "description", "동시성 테스트용 쿠폰");
    setField(coupon, "couponType", CouponType.FIXED_AMOUNT);
    setField(coupon, "status", Status.ACTIVE);
    setField(coupon, "discountValue", 1000);
    setField(coupon, "minOrderAmount", 10000);
    setField(coupon, "maxDiscountAmount", 1000);
    setField(coupon, "totalQuantity", totalQuantity);
    setField(coupon, "issuedQuantity", 0);
    setField(coupon, "validDays", 30);
    setField(coupon, "startDate", LocalDateTime.now().minusDays(1));
    setField(coupon, "endDate", LocalDateTime.now().plusDays(30));
    return coupon;
  }

  private void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

}
