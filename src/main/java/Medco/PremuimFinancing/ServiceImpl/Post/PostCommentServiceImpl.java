package Medco.PremuimFinancing.ServiceImpl.Post;

import Medco.PremuimFinancing.Dto.Request.Comment.CommentRequest;
import Medco.PremuimFinancing.Dto.Response.Comment.CommentResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserResponse;
import Medco.PremuimFinancing.Entity.Post.Comment;
import Medco.PremuimFinancing.Entity.Post.Post;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.Post.CommentRepository;
import Medco.PremuimFinancing.Repository.Post.PostRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Post.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostCommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostCommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentResponse create(UUID postId, CommentRequest request, String userUuid) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        UserEntity user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setActorId("user:" + userUuid);
        comment.setUser(user);
        comment.setComment(request.getComment());

        Comment savedComment = commentRepository.save(comment);
        return convertToResponse(savedComment);
    }

    @Override
    @Transactional
    public CommentResponse update(UUID commentId, CommentRequest request, String userUuid) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        UserEntity user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only comment creator can update this comment");
        }

        comment.setComment(request.getComment());
        Comment updatedComment = commentRepository.save(comment);
        return convertToResponse(updatedComment);
    }

    @Override
    @Transactional
    public void delete(UUID commentId, String userUuid) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        UserEntity user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only comment creator can delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    public Page<CommentResponse> getByPost(UUID postId, Pageable pageable, String userUuid) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        UserEntity user = null;
        if (userUuid != null) {
            user = userRepository.findByUserUuid(userUuid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }

        Page<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post, pageable);
        List<CommentResponse> responses = comments.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, comments.getTotalElements());
    }

    @Override
    public CommentResponse getById(UUID commentId, String userUuid) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        UserEntity user = null;
        if (userUuid != null) {
            user = userRepository.findByUserUuid(userUuid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
        return convertToResponse(comment);
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPost().getId());
        response.setComment(comment.getComment());
        if (comment.getCreatedAt() != null) {
            response.setCreatedAt(java.time.LocalDateTime.ofInstant(comment.getCreatedAt(), java.time.ZoneId.systemDefault()));
        }
        if (comment.getUpdatedAt() != null) {
            response.setUpdatedAt(java.time.LocalDateTime.ofInstant(comment.getUpdatedAt(), java.time.ZoneId.systemDefault()));
        }
        if (comment.getUser() != null) {
            response.setUser(mapUserToResponse(comment.getUser()));
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
