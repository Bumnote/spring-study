package kuke.board.comment.service.response;

import java.time.LocalDateTime;
import kuke.board.comment.entity.CommentV2;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommentResponseV2 {

  private Long commentId;
  private String content;
  private Long articleId;
  private Long writerId;
  private Boolean deleted;
  private String path;
  private LocalDateTime createdAt;

  public static CommentResponseV2 from(CommentV2 comment) {
    CommentResponseV2 response = new CommentResponseV2();
    response.commentId = comment.getCommentId();
    response.content = comment.getContent();
    response.articleId = comment.getArticleId();
    response.writerId = comment.getWriterId();
    response.deleted = comment.getDeleted();
    response.path = comment.getCommentPath().getPath();
    response.createdAt = comment.getCreatedAt();
    return response;
  }
}
