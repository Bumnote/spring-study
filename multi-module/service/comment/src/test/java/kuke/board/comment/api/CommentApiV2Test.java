package kuke.board.comment.api;

import java.util.List;
import kuke.board.comment.service.response.CommentPageResponseV2;
import kuke.board.comment.service.response.CommentResponseV2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

class CommentApiV2Test {

  RestClient restClient = RestClient.create("http://localhost:9001");

  @Test
  void create() {
    CommentResponseV2 response1 = create(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
    CommentResponseV2 response2 = create(new CommentCreateRequestV2(1L, "my comment2", response1.getPath(), 1L));
    CommentResponseV2 response3 = create(new CommentCreateRequestV2(1L, "my comment3", response2.getPath(), 1L));

    System.out.println("response1.getPath() = " + response1.getPath());
    System.out.println("response1.getCommentId() = " + response1.getCommentId());
    System.out.println("\tresponse2.getPath() = " + response2.getPath());
    System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
    System.out.println("\t\tresponse3.getPath() = " + response3.getPath());
    System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());

    /*
    response1.getPath() = 00004
    response1.getCommentId() = 266187650970443776
        response2.getPath() = 0000400000
        response2.getCommentId() = 266187651255656448
            response3.getPath() = 000040000000000
    		    response3.getCommentId() = 266187651318571008
    * */
  }

  CommentResponseV2 create(CommentCreateRequestV2 request) {
    return restClient.post()
        .uri("/v2/comments")
        .body(request)
        .retrieve()
        .body(CommentResponseV2.class);
  }

  @Test
  void read() {
    CommentResponseV2 response = restClient.get()
        .uri("/v2/comments/{commentId}", 266187650970443776L)
        .retrieve()
        .body(CommentResponseV2.class);

    System.out.println("response = " + response);
  }

  @Test
  void delete() {
    restClient.delete()
        .uri("/v2/comments/{commentId}", 266187651255656448L)
        .retrieve();
  }

  @Test
  void readAll() {
    CommentPageResponseV2 response = restClient.get()
        .uri("/v2/comments?articleId=1&pageSize=10&page=50000")
        .retrieve()
        .body(CommentPageResponseV2.class);

    System.out.println("response.getCommentCount() = " + response.getCommentCount());
    for (CommentResponseV2 comment : response.getComments()) {
      System.out.println("comment.getCommentId() = " + comment.getCommentId());
    }
    /*
    comment.getCommentId() = 266186765934551040
    comment.getCommentId() = 266186766848909312
    comment.getCommentId() = 266186766932795392
    comment.getCommentId() = 266186908851265536
    comment.getCommentId() = 266186909081952256

    comment.getCommentId() = 266186909153255424
    comment.getCommentId() = 266186976647995392
    comment.getCommentId() = 266186977025482752
    comment.getCommentId() = 266186977092591616
    comment.getCommentId() = 266187033044606976
    * */
  }

  @Test
  void readAllInfiniteScroll() {
    List<CommentResponseV2> responses1 = restClient.get()
        .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
        .retrieve()
        .body(new ParameterizedTypeReference<List<CommentResponseV2>>() {
        });

    System.out.println("firstPage");
    for (CommentResponseV2 response : responses1) {
      System.out.println("response.getCommentId() = " + response.getCommentId());
    }

    String lastPath = responses1.getLast().getPath();
    List<CommentResponseV2> responses2 = restClient.get()
        .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s".formatted(lastPath))
        .retrieve()
        .body(new ParameterizedTypeReference<List<CommentResponseV2>>() {
        });

    System.out.println("secondPage");
    for (CommentResponseV2 response : responses2) {
      System.out.println("response.getCommentId() = " + response.getCommentId());
    }
  }

  @Test
  void countTest() {
    CommentResponseV2 commentResponse = create(new CommentCreateRequestV2(2L, "my comment1", null, 1L));

    Long count1 = restClient.get()
        .uri("/v2/comments/articles/{articleId}/count", 2L)
        .retrieve()
        .body(Long.class);

    System.out.println("count1 = " + count1);

    restClient.delete()
        .uri("/v2/comments/{commentId}", commentResponse.getCommentId())
        .retrieve();

    Long count2 = restClient.get()
        .uri("/v2/comments/articles/{articleId}/count", 2L)
        .retrieve()
        .body(Long.class);

    System.out.println("count2 = " + count2);
  }

  @Getter
  @AllArgsConstructor
  public static class CommentCreateRequestV2 {

    private Long articleId;
    private String content;
    private String parentPath;
    private Long writerId;
  }
}
