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
public class ArticleUpdatedEventPayload implements EventPayload {

  private Long articleId;
  private String title;
  private String content;
  private Long boardId;
  private Long writerId;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
