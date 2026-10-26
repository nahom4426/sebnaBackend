package Medco.PremuimFinancing.Controller.Post;

import Medco.PremuimFinancing.Dto.Request.Comment.CommentRequest;
import Medco.PremuimFinancing.Dto.Response.Comment.CommentResponse;
import Medco.PremuimFinancing.Service.Post.CommentService;
import Medco.PremuimFinancing.utils.Pagination.PaginationUtils;
import Medco.PremuimFinancing.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sebna/post-comments")
public class PostCommentController {

    private final CommentService commentService;

    public PostCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponse>> getByPost(
            @PathVariable UUID postId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Pageable pageable = PaginationUtils.paginateResource(page, limit, "createdAt", "asc");
        var auth = SecurityUtils.getAuthenticatedUserOrNull();
        Page<CommentResponse> comments = commentService.getByPost(postId, pageable, auth == null ? null : auth.getUserUuid());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getById(@PathVariable UUID commentId) {
        var auth = SecurityUtils.getAuthenticatedUserOrNull();
        CommentResponse comment = commentService.getById(commentId, auth == null ? null : auth.getUserUuid());
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/{postId}")
    @PreAuthorize("hasAuthority('ROLE_create_comment')")
    public ResponseEntity<CommentResponse> create(
            @PathVariable UUID postId,
            @Valid @RequestBody CommentRequest request) {
        var auth = SecurityUtils.getAuthenticatedUser();
        CommentResponse created = commentService.create(postId, request, auth.getUserUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAuthority('ROLE_update_comment')")
    public ResponseEntity<CommentResponse> update(
            @PathVariable UUID commentId,
            @Valid @RequestBody CommentRequest request) {
        var auth = SecurityUtils.getAuthenticatedUser();
        CommentResponse updated = commentService.update(commentId, request, auth.getUserUuid());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAuthority('ROLE_delete_comment')")
    public ResponseEntity<Void> delete(@PathVariable UUID commentId) {
        var auth = SecurityUtils.getAuthenticatedUser();
        commentService.delete(commentId, auth.getUserUuid());
        return ResponseEntity.noContent().build();
    }
}
