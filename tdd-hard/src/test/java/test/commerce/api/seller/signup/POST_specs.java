package test.commerce.api.seller.signup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import spring.tddhard.CommerceApiApp;
import spring.tddhard.commerce.CreateSellerCommand;

@SpringBootTest(
    classes = CommerceApiApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)

@DisplayName("POST /seller/signUp")
class POST_specs {

  @Test
  void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
      @Autowired TestRestTemplate client
  ) throws Exception {
    // given
    var command = new CreateSellerCommand(
        "seller@test.com",
        "seller",
        "password"
    );

    // when
    ResponseEntity<Void> response = client.postForEntity(
        "/seller/signUp",
        command,
        Void.class);

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(204);
  }


  @Test
  void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
      @Autowired TestRestTemplate client
  ) {

    // given
    var command = new CreateSellerCommand(
        null,
        "seller",
        "password"
    );

    // when
    ResponseEntity<Void> response = client.postForEntity(
        "/seller/signUp",
        command,
        Void.class);

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(400);
  }
}