package spring.tddhard.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.tddhard.result.AccessTokenCarrier;

@RestController
public record SellerIssueTokenController() {

  @PostMapping("/seller/issueToken")
  AccessTokenCarrier issueToken() {
    return new AccessTokenCarrier("token");
  }

}
