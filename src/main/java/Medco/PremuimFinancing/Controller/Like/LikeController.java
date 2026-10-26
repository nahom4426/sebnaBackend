package Medco.PremuimFinancing.Controller.Like;

import Medco.PremuimFinancing.Entity.Post.PostLike;
import Medco.PremuimFinancing.Service.Like.PostLikeService;
import Medco.PremuimFinancing.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sebna/likes")
public class LikeController {

    private final PostLikeService postLikeService;

    public LikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping("/post/{postId}")
    @PreAuthorize("hasAnyAuthority('ROLE_like_post','ROLE_unlike_post')")
    public PostLike like(@PathVariable UUID postId) {
        var auth = SecurityUtils.getAuthenticatedUser();
        return postLikeService.like(postId, auth.getUserUuid());
    }

    @DeleteMapping("/post/{postId}")
    @PreAuthorize("hasAnyAuthority('ROLE_like_post','ROLE_unlike_post')")
    public void unlike(@PathVariable UUID postId) {
        var auth = SecurityUtils.getAuthenticatedUser();
        postLikeService.unlike(postId, auth.getUserUuid());
    }

    @GetMapping("/post/{postId}/me")
    public Map<String, Boolean> likedByMe(@PathVariable UUID postId) {
        var auth = SecurityUtils.getAuthenticatedUser();
        return Map.of("liked", postLikeService.likedByUser(postId, auth.getUserUuid()));
    }
}
