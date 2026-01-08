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
public class ArticleUnlikedEventPayload implements EventPayload {

  private Long articleLikeId;
  private Long articleId;
  private Long userId;
  private LocalDateTime createdAt;
  private Long articleLikedCount;
}
