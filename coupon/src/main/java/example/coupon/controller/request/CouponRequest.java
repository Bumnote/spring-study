package example.coupon.controller.request;

public record CouponRequest(
    Long userId,
    Long orderId,
    Integer orderAmount
) {

}
