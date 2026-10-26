package Medco.PremuimFinancing.Service.Post;

import Medco.PremuimFinancing.Dto.Response.PostLike.PostLikeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostLikeService {
    PostLikeResponse toggleLike(UUID postId, String actorId, String userUuid);
    Page<PostLikeResponse> getByPost(UUID postId, Pageable pageable);
    long countLikes(UUID postId);
    boolean isLiked(UUID postId, String actorId);
}
