package Medco.PremuimFinancing.Service.Post;

import Medco.PremuimFinancing.Dto.Request.Post.PostCreateRequest;
import Medco.PremuimFinancing.Dto.Request.Post.PostUpdateRequest;
import Medco.PremuimFinancing.Dto.Response.Post.PostResponse;
import Medco.PremuimFinancing.Entity.Post.Post;
import Medco.PremuimFinancing.Enums.Post.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Page<PostResponse> landing(Pageable pageable, String userUuid, PostCategory category);
    Page<PostResponse> listByUser(Long userId, Pageable pageable);
    PostResponse create(PostCreateRequest request, MultipartFile image, String creatorUserUuid);
    PostResponse update(UUID postId, PostUpdateRequest request, MultipartFile image, String userUuid);
    void delete(UUID postId, String userUuid);
    PostResponse getById(UUID postId, String userUuid);
}
