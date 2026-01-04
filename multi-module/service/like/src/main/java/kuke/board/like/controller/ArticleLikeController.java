package kuke.board.like.controller;

import kuke.board.like.service.ArticleLikeService;
import kuke.board.like.service.response.ArticleLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/article-likes")
public class ArticleLikeController {

  private final ArticleLikeService articleLikeService;

  @GetMapping("/articles/{articleId}/users/{userId}")
  public ArticleLikeResponse read(
      @PathVariable("articleId") Long articleId,
      @PathVariable("userId") Long userId
  ) {
    return articleLikeService.read(articleId, userId);
  }

  @PostMapping("/articles/{articleId}/users/{userId}")
  public void like(
      @PathVariable("articleId") Long articleId,
      @PathVariable("userId") Long userId) {
    articleLikeService.like(articleId, userId);
  }

  @DeleteMapping("/articles/{articleId}/users/{userId}")
  public void unlike(
      @PathVariable("articleId") Long articleId,
      @PathVariable("userId") Long userId
  ) {
    articleLikeService.unlike(articleId, userId);
  }
}
