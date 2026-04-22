package example.coupon.api.service.coupon.request;

public record CouponRequest(
    Long userId,
    Long orderId,
    Integer orderAmount
) {

}
