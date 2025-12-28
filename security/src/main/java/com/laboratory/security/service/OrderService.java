package com.laboratory.security.service;

import com.laboratory.security.domain.model.Product;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  public void createOrder(Product product) {
    System.out.println("create order for " + product);
  }
}