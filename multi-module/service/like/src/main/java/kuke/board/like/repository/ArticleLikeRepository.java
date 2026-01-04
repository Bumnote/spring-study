package kuke.board.like.repository;

import java.util.Optional;
import kuke.board.like.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

  Optional<ArticleLike> findByArticleIdAndUserId(Long articleId, Long userId);

}
