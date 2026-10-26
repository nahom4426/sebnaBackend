package Medco.PremuimFinancing.ServiceImpl.Post;

import Medco.PremuimFinancing.Dto.Response.PostLike.PostLikeResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserResponse;
import Medco.PremuimFinancing.Entity.Post.Post;
import Medco.PremuimFinancing.Entity.Post.PostLike;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.Post.PostLikeRepository;
import Medco.PremuimFinancing.Repository.Post.PostRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Post.PostLikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("postPostLikeServiceImpl")
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostLikeServiceImpl(PostLikeRepository postLikeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PostLikeResponse toggleLike(UUID postId, String actorId, String userUuid) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        UserEntity user = null;
        if (userUuid != null) {
            user = userRepository.findByUserUuid(userUuid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } else {
            // anonymous device likes allowed
        }

        var existingLike = postLikeRepository.findByPostAndActorId(post, actorId);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            return null;
        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setActorId(actorId);
            like.setUser(user);
            PostLike savedLike = postLikeRepository.save(like);
            return convertToResponse(savedLike);
        }
    }

    @Override
    public Page<PostLikeResponse> getByPost(UUID postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Page<PostLike> likes = postLikeRepository.findByPostOrderByCreatedAtDesc(post, pageable);
        List<PostLikeResponse> responses = likes.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, likes.getTotalElements());
    }

    @Override
    public long countLikes(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return postLikeRepository.countByPost(post);
    }

    @Override
    public boolean isLiked(UUID postId, String actorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return postLikeRepository.existsByPostAndActorId(post, actorId);
    }

    private PostLikeResponse convertToResponse(PostLike like) {
        PostLikeResponse response = new PostLikeResponse();
        response.setId(like.getId());
        response.setPostId(like.getPost().getId());
        if (like.getCreatedAt() != null) {
            response.setCreatedAt(java.time.LocalDateTime.ofInstant(like.getCreatedAt(), java.time.ZoneId.systemDefault()));
        }
        if (like.getUser() != null) {
            response.setUser(mapUserToResponse(like.getUser()));
        }
        return response;
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
