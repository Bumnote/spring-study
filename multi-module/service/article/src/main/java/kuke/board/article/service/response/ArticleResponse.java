package kuke.board.article.service.response;

import java.time.LocalDateTime;
import kuke.board.article.entity.Article;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleResponse {

  private Long articleId;
  private String title;
  private String content;
  private Long boardId; // shard key
  private Long writerId;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  // 팩토리 메소드 -> Article 객체를 받아서 응답객체로 변환
  public static ArticleResponse from(Article article) {
    ArticleResponse response = new ArticleResponse();
    response.articleId = article.getArticleId();
    response.title = article.getTitle();
    response.content = article.getContent();
    response.boardId = article.getBoardId();
    response.writerId = article.getWriterId();
    response.createdAt = article.getCreatedAt();
    response.modifiedAt = article.getModifiedAt();
    return response;
  }

}
