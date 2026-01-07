package kuke.board.comment.controller;

import java.util.List;
import kuke.board.comment.service.CommentServiceV2;
import kuke.board.comment.service.request.CommentCreateRequestV2;
import kuke.board.comment.service.response.CommentPageResponseV2;
import kuke.board.comment.service.response.CommentResponseV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/comments")
public class CommentControllerV2 {

  private final CommentServiceV2 commentService;

  @GetMapping("/{commentId}")
  public CommentResponseV2 read(
      @PathVariable("commentId") Long commentId
  ) {
    return commentService.read(commentId);
  }

  @PostMapping
  public CommentResponseV2 create(@RequestBody CommentCreateRequestV2 request) {
    return commentService.create(request);
  }

  @DeleteMapping("/{commentId}")
  public void delete(@PathVariable("commentId") Long commentId) {
    commentService.delete(commentId);
  }

  @GetMapping
  public CommentPageResponseV2 readAll(
      @RequestParam("articleId") Long articleId,
      @RequestParam("page") Long page,
      @RequestParam("pageSize") Long pageSize
  ) {
    return commentService.readAll(articleId, page, pageSize);
  }

  @GetMapping("/infinite-scroll")
  public List<CommentResponseV2> readAllInfiniteScroll(
      @RequestParam("articleId") Long articleId,
      @RequestParam(value = "lastPath", required = false) String lastPath,
      @RequestParam("pageSize") Long pageSize
  ) {
    return commentService.readAllInfiniteScroll(articleId, lastPath, pageSize);
  }

  @GetMapping("/articles/{articleId}/count")
  public Long count(@PathVariable Long articleId) {
    return commentService.count(articleId);
  }
}