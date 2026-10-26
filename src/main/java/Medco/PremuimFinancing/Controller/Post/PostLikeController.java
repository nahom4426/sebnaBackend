package Medco.PremuimFinancing.Controller.Post;

import Medco.PremuimFinancing.Dto.Response.PostLike.PostLikeResponse;
import Medco.PremuimFinancing.Service.Post.PostLikeService;
import Medco.PremuimFinancing.utils.Pagination.PaginationUtils;
import Medco.PremuimFinancing.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sebna/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<PostLikeResponse>> getByPost(
            @PathVariable UUID postId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Pageable pageable = PaginationUtils.paginateResource(page, limit, "createdAt", "desc");
        Page<PostLikeResponse> likes = postLikeService.getByPost(postId, pageable);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Map<String, Long>> countLikes(@PathVariable UUID postId) {
        long count = postLikeService.countLikes(postId);
        Map<String, Long> response = new HashMap<>();
        response.put("likeCount", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/post/{postId}/user-liked")
    public ResponseEntity<Map<String, Boolean>> isLikedByUser(
            @PathVariable UUID postId,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId
    ) {
        var auth = SecurityUtils.getAuthenticatedUserOrNull();

        String actorId = null;
        if (auth != null) {
            actorId = "user:" + auth.getUserUuid();
        } else if (deviceId != null && !deviceId.isBlank()) {
            actorId = "device:" + deviceId;
        }

        boolean liked = actorId != null && postLikeService.isLiked(postId, actorId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable UUID postId,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Missing X-Device-Id for anonymous like"
            ));
        }

        PostLikeResponse result = postLikeService.toggleLike(postId, actorId, userUuid);
        boolean liked = result != null;
        long likeCount = postLikeService.countLikes(postId);

        return ResponseEntity.ok(Map.of(
                "liked", liked,
                "likeCount", likeCount
        ));
    }
}
