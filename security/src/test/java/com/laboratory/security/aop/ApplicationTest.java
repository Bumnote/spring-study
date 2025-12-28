package com.laboratory.security.aop;

import com.laboratory.security.domain.model.Product;
import com.laboratory.security.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTest {

  @Autowired
  private OrderService orderService;

  @Test
  void test_log_aspect() {
    orderService.createOrder(new Product(1, "computer", 4_000_000));
  }

}