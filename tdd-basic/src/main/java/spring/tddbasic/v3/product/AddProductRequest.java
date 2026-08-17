package spring.tddbasic.v3.product;

import org.springframework.util.Assert;

// Request 객체는 Record 클래스로 정의해서 사용하는 것이 좋습니다.
record AddProductRequest(String name, int price, DiscountPolicy discountPolicy) {

  AddProductRequest(String name, int price, DiscountPolicy discountPolicy) {
    this.name = name;
    this.price = price;
    this.discountPolicy = discountPolicy;

    Assert.hasText(name, "상품명은 필수입니다.");
    Assert.isTrue(price > 0, "상품 가격은 0보다 커야 합니다.");
    Assert.notNull(discountPolicy, "할인 정책은 필수입니다.");
  }
}
