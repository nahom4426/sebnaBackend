package Medco.PremuimFinancing.Service.Comment;

import Medco.PremuimFinancing.Dto.Response.Comment.CommentResponse;
import Medco.PremuimFinancing.Entity.Post.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    List<Comment> listByPost(UUID postId);
    List<CommentResponse> listByPostResponse(UUID postId);
    Page<CommentResponse> listByPostResponse(UUID postId, Pageable pageable);
    CommentResponse add(UUID postId, String userUuid, String content, String actorId);
    void delete(UUID commentId);
}
