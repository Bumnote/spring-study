package kuke.board.article.api;


import java.util.List;
import kuke.board.article.service.response.ArticlePageResponse;
import kuke.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

class ArticleApiTest {

  RestClient restClient = RestClient.create("http://localhost:9000");

  @Test
  void createTest() {

    ArticleResponse response = create(new ArticleCreateRequest(
        "hi", "my content", 1L, 1L
    ));
    System.out.println("response = " + response);
  }

  ArticleResponse create(ArticleCreateRequest request) {
    return restClient.post()
        .uri("/v1/articles")
        .body(request)
        .retrieve()
        .body(ArticleResponse.class);
  }

  @Test
  void readTest() {
    ArticleResponse response = read(179529474428891136L);
    System.out.println("response = " + response);
  }

  ArticleResponse read(Long articleId) {
    return restClient.get()
        .uri("/v1/articles/{articleId}", articleId)
        .retrieve()
        .body(ArticleResponse.class);
  }

  @Test
  void updateTest() {
    update(179529474428891136L);
    ArticleResponse response = read(179529474428891136L);
    System.out.println("response = " + response);
  }

  void update(Long articleId) {
    restClient.put()
        .uri("/v1/articles/{articleId}", articleId)
        .body(new ArticleUpdateRequest("hi 33", "my content 22"))
        .retrieve()
        .body(ArticleResponse.class);
  }

  @Test
  void deleteTest() {
    restClient.delete()
        .uri("/v1/articles/{articleId}", 179529474428891136L)
        .retrieve();
  }

  @Test
  void readAllTest() {
    ArticlePageResponse response = restClient.get()
        .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
        .retrieve()
        .body(ArticlePageResponse.class);

    System.out.println("response.getArticlesCount() = " + response.getArticleCount());
    for (ArticleResponse article : response.getArticles()) {
      System.out.println("article = " + article.getArticleId());
    }
  }

  @Test
  void readAllInfiniteScrollTest() {
    List<ArticleResponse> articles1 = restClient.get()
        .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
        .retrieve()
        .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
        });

    System.out.println("firstPage");
    for (ArticleResponse articleResponse : articles1) {
      System.out.println("articleResponse.getArticleId() = " + articleResponse.getArticleId());
    }

    Long lastArticleId = articles1.getLast().getArticleId();
    List<ArticleResponse> articles2 = restClient.get()
        .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
        .retrieve()
        .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
        });

    System.out.println("secondPage");
    for (ArticleResponse articleResponse : articles2) {
      System.out.println("articleResponse.getArticleId() = " + articleResponse.getArticleId());
    }
  }

  @Getter
  @AllArgsConstructor
  public static class ArticleCreateRequest {

    private String title;
    private String content;
    private Long writerId;
    private Long boardId;
  }

  @Getter
  @AllArgsConstructor
  public static class ArticleUpdateRequest {

    private String title;
    private String content;
  }

}
