package Medco.PremuimFinancing.ServiceImpl.Post;

import Medco.PremuimFinancing.Dto.Request.Post.PostCreateRequest;
import Medco.PremuimFinancing.Dto.Request.Post.PostUpdateRequest;
import Medco.PremuimFinancing.Dto.Response.Post.PostResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserResponse;
import Medco.PremuimFinancing.Entity.Post.Post;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Enums.Post.PostCategory;
import Medco.PremuimFinancing.Repository.Post.CommentRepository;
import Medco.PremuimFinancing.Repository.Post.PostLikeRepository;
import Medco.PremuimFinancing.Repository.Post.PostRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Post.PostService;
import Medco.PremuimFinancing.utils.Image.ImageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    public PostServiceImpl(PostRepository postRepository,
                         UserRepository userRepository,
                         PostLikeRepository postLikeRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Page<PostResponse> landing(Pageable pageable, String userUuid, PostCategory category) {
        Page<Post> posts;
        if (category == null) {
            posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            posts = postRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
        }
        UserEntity user = (userUuid == null) ? null : userRepository.findByUserUuid(userUuid).orElse(null);
        return convertToResponsePage(posts, pageable, user);
    }

    @Override
    public Page<PostResponse> listByUser(Long userId, Pageable pageable) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Page<Post> posts = postRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return convertToResponsePage(posts, pageable, user);
    }

    @Override
    @Transactional
    public PostResponse create(PostCreateRequest request, MultipartFile image, String creatorUserUuid) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());

        UserEntity user = userRepository.findByUserUuid(creatorUserUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (image != null && !image.isEmpty()) {
            try {
                byte[] imageBytes = image.getBytes();
                if (imageBytes.length > 5242880) {
                    throw new IllegalArgumentException("Image size exceeds 5MB limit");
                }
                post.setImage(ImageUtils.compressImage(imageBytes));
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process image: " + e.getMessage());
            }
        }

        post.setUser(user);
        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost, user);
    }

    @Override
    @Transactional
    public PostResponse update(UUID postId, PostUpdateRequest request, MultipartFile image, String userUuid) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        UserEntity user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only post creator can update this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());

        if (image != null && !image.isEmpty()) {
            try {
                byte[] imageBytes = image.getBytes();
                if (imageBytes.length > 5242880) {
                    throw new IllegalArgumentException("Image size exceeds 5MB limit");
                }
                post.setImage(ImageUtils.compressImage(imageBytes));
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process image: " + e.getMessage());
            }
        }

        Post updatedPost = postRepository.save(post);
        return convertToResponse(updatedPost, user);
    }

    @Override
    @Transactional
    public void delete(UUID postId, String userUuid) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        UserEntity user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only post creator can delete this post");
        }

        postRepository.delete(post);
    }

    @Override
    public PostResponse getById(UUID postId, String userUuid) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setViews((post.getViews() == null ? 0L : post.getViews()) + 1L);
        Post saved = postRepository.save(post);

        UserEntity user = (userUuid == null) ? null : userRepository.findByUserUuid(userUuid).orElse(null);
        return convertToResponse(saved, user);
    }

    private PostResponse convertToResponse(Post post, UserEntity currentUser) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCategory(post.getCategory());
        response.setViews(post.getViews());
        if (post.getCreatedAt() != null) {
            response.setCreatedAt(java.time.LocalDateTime.ofInstant(post.getCreatedAt(), java.time.ZoneId.systemDefault()));
        }
        if (post.getUpdatedAt() != null) {
            response.setUpdatedAt(java.time.LocalDateTime.ofInstant(post.getUpdatedAt(), java.time.ZoneId.systemDefault()));
        }

        response.setUser(mapUserToResponse(post.getUser()));
        response.setLikeCount(postLikeRepository.countByPost(post));
        response.setCommentCount(commentRepository.countByPost(post));

        if (currentUser != null) {
            response.setUserLiked(postLikeRepository.existsByPostAndUser(post, currentUser));
        }

        if (post.getImage() != null && post.getImage().length > 0) {
            byte[] decompressed = ImageUtils.decompressImage(post.getImage());
            String base64 = Base64.getEncoder().encodeToString(decompressed);
            response.setImageBase64("data:image/jpeg;base64," + base64);
        }

        return response;
    }

    private Page<PostResponse> convertToResponsePage(Page<Post> posts, Pageable pageable, UserEntity currentUser) {
        List<PostResponse> responses = posts.getContent().stream()
                .map(post -> convertToResponse(post, currentUser))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }

    private UserResponse mapUserToResponse(UserEntity user) {
        UserResponse response = new UserResponse();
        response.setUserUuid(user.getUserUuid());
        response.setFirstName(user.getFirstName());
        response.setFatherName(user.getFatherName());
        response.setGrandFatherName(user.getGrandFatherName());
        response.setEmail(user.getEmail());
        response.setMobilePhone(user.getMobilePhone());
        return response;
    }

}
