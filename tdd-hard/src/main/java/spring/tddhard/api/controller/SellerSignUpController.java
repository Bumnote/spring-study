package spring.tddhard.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.tddhard.commerce.CreateSellerCommand;

@RestController
public record SellerSignUpController() {

  @PostMapping("/seller/signUp")
  ResponseEntity<Void> signUp(@RequestBody CreateSellerCommand command) {
    if (command.email() == null) {
      return ResponseEntity.badRequest().build();
    } else {
      return ResponseEntity.noContent().build();
    }
  }


}
