package kuke.board.like.entity;

import static java.time.LocalDateTime.now;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "article_like")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = PROTECTED)
public class ArticleLike {

  @Id
  private Long articleLikeId;
  private Long articleId; // shard key
  private Long userId;
  private LocalDateTime createdAt;

  public static ArticleLike create(Long articleLikeId, Long articleId, Long userId) {
    ArticleLike articleLike = new ArticleLike();
    articleLike.articleLikeId = articleLikeId;
    articleLike.articleId = articleId;
    articleLike.userId = userId;
    articleLike.createdAt = now();
    return articleLike;
  }
}
