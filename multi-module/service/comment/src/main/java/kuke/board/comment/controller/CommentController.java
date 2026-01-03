package kuke.board.comment.controller;

import java.util.List;
import kuke.board.comment.service.CommentService;
import kuke.board.comment.service.request.CommentCreateRequest;
import kuke.board.comment.service.response.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
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
@RequestMapping("/v1/comments")
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/{commentId}")
  public CommentResponse read(
      @PathVariable("commentId") Long commentId
  ) {
    return commentService.read(commentId);
  }

  @PostMapping
  public CommentResponse create(@RequestBody CommentCreateRequest request) {
    return commentService.create(request);
  }

  @DeleteMapping("/{commentId}")
  public void delete(@PathVariable("commentId") Long commentId) {
    commentService.delete(commentId);
  }

  @GetMapping
  public CommentPageResponse readAll(
      @RequestParam("articleId") Long articleId,
      @RequestParam("page") Long page,
      @RequestParam("pageSize") Long pageSize
  ) {
    return commentService.readAll(articleId, page, pageSize);
  }

  @GetMapping("/infinite-scroll")
  public List<CommentResponse> readAll(
      @RequestParam("articleId") Long articleId,
      @RequestParam(value = "lastParentCommentId", required = false) Long lastParentCommentId,
      @RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
      @RequestParam("pageSize") Long pageSize
  ) {
    return commentService.readAll(articleId, lastParentCommentId, lastCommentId, pageSize);
  }
}