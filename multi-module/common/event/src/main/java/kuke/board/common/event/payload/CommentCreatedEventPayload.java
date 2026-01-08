package kuke.board.common.event.payload;

import java.time.LocalDateTime;
import kuke.board.common.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreatedEventPayload implements EventPayload {

  private Long commentId;
  private String content;
  private String path;
  private Long articleId;
  private Long writerId;
  private Boolean deleted;
  private LocalDateTime createdAt;
  private Long articleCommentCount;
}
