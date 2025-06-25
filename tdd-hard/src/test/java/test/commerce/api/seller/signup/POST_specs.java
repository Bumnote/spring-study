package test.commerce.api.seller.signup;

import static org.assertj.core.api.Assertions.assertThat;
import static test.commerce.EmailGenerator.generateEmail;
import static test.commerce.PasswordGenerator.generatePassword;
import static test.commerce.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.tddhard.CommerceApiApp;
import spring.tddhard.Seller;
import spring.tddhard.SellerRepository;
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
        generateEmail(),
        generateUsername(),
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
        generateUsername(),
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

  @ParameterizedTest
  @ValueSource(strings = {
      "invalid-email",
      "invalid-email@",
      "invalid-email@test",
      "invalid-email@test.",
      "invalid-email@.com",
  })
  void email_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
      String email,
      @Autowired TestRestTemplate client
  ) {

    // given
    var command = new CreateSellerCommand(
        email,
        generateUsername(),
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

  @Test
  void username_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
      @Autowired TestRestTemplate client
  ) {

    // given
    var command = new CreateSellerCommand(
        generateEmail(),
        null,
        "password"
    );

    // when
    ResponseEntity<Void> response = client.postForEntity(
        "/seller/signUp",
        command,
        Void.class
    );

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(400);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "",
      "se",
      "seller ",
      "seller.",
      "seller!",
      "seller@",
  })
  void username_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
      String username,
      @Autowired TestRestTemplate client
  ) {

    // given
    var command = new CreateSellerCommand(
        generateEmail(),
        username,
        "password"
    );

    // when
    ResponseEntity<Void> response = client.postForEntity(
        "/seller/signUp",
        command,
        Void.class
    );

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(400);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "seller",
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
      "0123456789",
      "seller_",
      "seller-",
  })
  void username_속성이_올바른_형식을_따르면_204_No_Content_상태코드를_반환한다(
      String username,
      @Autowired TestRestTemplate client
  ) {

    // given
    var command = new CreateSellerCommand(
        generateEmail(),
        username,
        "password"
    );

    // when
    ResponseEntity<Void> response = client.postForEntity(
        "/seller/signUp",
        command,
        Void.class
    );

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(204);
  }

  @Test
  void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
      @Autowired TestRestTemplate client
  ) {

    // given
    var command = new CreateSellerCommand(
        generateEmail(),
        generateUsername(),
        null
    );

    // when
    ResponseEntity<Void> response = client.postForEntity(
        "/seller/signUp",
        command,
        Void.class
    );

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(400);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "",
      "pass",
      "pass123"
  })
  void password_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
      String password,
      @Autowired TestRestTemplate client
  ) {

    // given
    var command = new CreateSellerCommand(
        generateEmail(),
        generateUsername(),
        password
    );

    // when
    ResponseEntity<Void> response = client.postForEntity(
        "/seller/signUp",
        command,
        Void.class
    );

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(400);
  }

  @Test
  void email_속성이_이미_존재하는_이메일_주소가_지정되면_400_Bad_Request_상태코드를_반환한다(
      @Autowired TestRestTemplate client
  ) {

    // given
    String email = generateEmail();
    client.postForEntity(
        "/seller/signUp",
        new CreateSellerCommand(email, generateUsername(), "password"),
        Void.class);

    // when
    ResponseEntity<Void> response = client.postForEntity(
        "/seller/signUp",
        new CreateSellerCommand(email, generateUsername(), "password"),
        Void.class
    );

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(400);
  }

  @Test
  void username_속성이_이미_존재하는_사용자_이름이_지정되면_400_Bad_Request_상태코드를_반환한다(
      @Autowired TestRestTemplate client
  ) {

    // given
    String username = generateUsername();

    client.postForEntity(
        "/seller/signUp",
        new CreateSellerCommand(generateEmail(), username, "password"),
        Void.class);

    // when
    ResponseEntity<Void> response = client.postForEntity(
        "/seller/signUp",
        new CreateSellerCommand(generateEmail(), username, "password"),
        Void.class
    );

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(400);
  }

  @Test
  void 비밀번호를_올바르게_암호화한다(
      @Autowired TestRestTemplate client,
      @Autowired SellerRepository repository,
      @Autowired PasswordEncoder encoder
  ) {

    // given
    var command = new CreateSellerCommand(
        generateEmail(),
        generateUsername(),
        generatePassword()
    );

    // when
    client.postForEntity("/seller/signUp", command, Void.class);

    // then
    Seller seller = repository
        .findAll()
        .stream()
        .filter(x -> x.getEmail().equals(command.email()))
        .findFirst()
        .orElseThrow();

    String actual = seller.getHashedPassword();
    assertThat(actual).isNotNull();
    assertThat(encoder.matches(command.password(), actual)).isTrue();
  }
}