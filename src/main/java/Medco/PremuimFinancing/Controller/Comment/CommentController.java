package Medco.PremuimFinancing.Controller.Comment;

import Medco.PremuimFinancing.Dto.Request.Comment.CommentRequest;
import Medco.PremuimFinancing.Dto.Response.Comment.CommentResponse;
import Medco.PremuimFinancing.Service.Comment.CommentService;
import Medco.PremuimFinancing.utils.Pagination.PaginationUtils;
import Medco.PremuimFinancing.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sebna/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/post/{postId}")
    public Page<CommentResponse> listByPost(
            @PathVariable UUID postId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Pageable pageable = PaginationUtils.paginateResource(page, limit, "createdAt", "asc");
        return commentService.listByPostResponse(postId, pageable);
    }

    @PostMapping("/post/{postId}")
    public CommentResponse add(
            @PathVariable UUID postId,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
            @Valid @RequestBody CommentRequest request
    ) {
        var auth = SecurityUtils.getAuthenticatedUserOrNull();

        String actorId;
        String userUuid = null;
        if (auth != null) {
            actorId = "user:" + auth.getUserUuid();
            userUuid = auth.getUserUuid();
        } else if (deviceId != null && !deviceId.isBlank()) {
            actorId = "device:" + deviceId;
        } else {
            throw new IllegalArgumentException("Missing X-Device-Id for anonymous comment");
        }

        return commentService.add(postId, userUuid, request.getComment(), actorId);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAuthority('ROLE_delete_comment')")
    public void delete(@PathVariable UUID commentId) {
        commentService.delete(commentId);
    }
}
