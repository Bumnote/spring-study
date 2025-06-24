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
    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    if (command.email() == null) {
      return ResponseEntity.badRequest().build();
    } else if (command.email().contains("@") == false) {
      return ResponseEntity.badRequest().build();
    } else if (command.email().endsWith("@")) {
      return ResponseEntity.badRequest().build();
    } else if (command.email().matches(emailRegex) == false) {
      return ResponseEntity.badRequest().build();
    } else {
      return ResponseEntity.noContent().build();
    }
  }


}
