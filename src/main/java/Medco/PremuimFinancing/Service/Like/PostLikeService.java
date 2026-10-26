package Medco.PremuimFinancing.Service.Like;

import Medco.PremuimFinancing.Entity.Post.PostLike;

import java.util.UUID;

public interface PostLikeService {
    PostLike like(UUID postId, String userUuid);
    void unlike(UUID postId, String userUuid);
    long count(UUID postId);
    boolean likedByUser(UUID postId, String userUuid);
}
