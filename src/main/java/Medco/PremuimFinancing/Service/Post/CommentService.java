package Medco.PremuimFinancing.Service.Post;

import Medco.PremuimFinancing.Dto.Request.Comment.CommentRequest;
import Medco.PremuimFinancing.Dto.Response.Comment.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {
    CommentResponse create(UUID postId, CommentRequest request, String userUuid);
    CommentResponse update(UUID commentId, CommentRequest request, String userUuid);
    void delete(UUID commentId, String userUuid);
    Page<CommentResponse> getByPost(UUID postId, Pageable pageable, String userUuid);
    CommentResponse getById(UUID commentId, String userUuid);
}
