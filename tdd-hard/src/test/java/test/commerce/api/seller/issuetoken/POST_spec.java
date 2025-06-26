package test.commerce.api.seller.issuetoken;

import static org.assertj.core.api.Assertions.assertThat;
import static test.commerce.EmailGenerator.generateEmail;
import static test.commerce.PasswordGenerator.generatePassword;
import static test.commerce.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import spring.tddhard.CommerceApiApp;
import spring.tddhard.commerce.CreateSellerCommand;
import spring.tddhard.query.IssueSellerToken;
import spring.tddhard.result.AccessTokenCarrier;

@SpringBootTest(
    classes = CommerceApiApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("POST /seller/issueToken")
public class POST_spec {

  @Autowired
  private ClientHttpRequestFactoryBuilder<?> clientHttpRequestFactoryBuilder;

  @Test
  void 올바르게_요청하면_200_OK_상태코드를_반환한다(
      @Autowired TestRestTemplate client
  ) {

    // given
    String email = generateEmail();
    String password = generatePassword();

    client.postForEntity(
        "/seller/signUp",
        new CreateSellerCommand(email, generateUsername(), password),
        AccessTokenCarrier.class
    );

    // when
    ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
        "/seller/issueToken",
        new IssueSellerToken(email, password),
        AccessTokenCarrier.class
    );

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(200);
  }
  
  @Test
  void 올바르게_요청하면_접근_토큰을_반환한다(
      @Autowired TestRestTemplate client
  ) {

    // given
    String email = generateEmail();
    String password = generatePassword();

    client.postForEntity(
        "/seller/signUp",
        new CreateSellerCommand(email, generateUsername(), password),
        AccessTokenCarrier.class
    );

    // when
    ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
        "/seller/issueToken",
        new IssueSellerToken(email, password),
        AccessTokenCarrier.class
    );

    // then
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().accessToken()).isNotNull();
  }

}
