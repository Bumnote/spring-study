package kuke.board.comment.service.request;

public class CommentCreateRequest {

  private Long articleId;
  private String content;
  private Long parentCommentId;
  private Long writerId;
}
