package kuke.board.hotarticle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountRepository {

  private final StringRedisTemplate redisTemplate;

  // hot-article::article::{articleId}::view-count
  private static final String KEY_FORMAT = "hot-article::article::%s::view-count";

  public void createOrUpdate(Long articleId, Long viewCount) {
    redisTemplate.opsForValue().set(generateKey(articleId), String.valueOf(viewCount));
  }

  public Long read(Long articleId) {
    String result = redisTemplate.opsForValue().get(generateKey(articleId));
    return result == null ? 0L : Long.valueOf(result);
  }

  private String generateKey(Long articleId) {
    return KEY_FORMAT.formatted(articleId);
  }

}
