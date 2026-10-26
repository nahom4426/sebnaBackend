package Medco.PremuimFinancing.ServiceImpl.Comment;

import Medco.PremuimFinancing.Dto.Response.Comment.CommentResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserResponse;
import Medco.PremuimFinancing.Entity.Post.Comment;
import Medco.PremuimFinancing.Entity.Post.Post;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.Post.CommentRepository;
import Medco.PremuimFinancing.Repository.Post.PostRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Comment.CommentService;
import Medco.PremuimFinancing.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> listByPost(UUID postId) {
        Post p = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return commentRepository.findByPostOrderByCreatedAtAsc(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> listByPostResponse(UUID postId) {
        return listByPost(postId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> listByPostResponse(UUID postId, Pageable pageable) {
        Post p = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        Page<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(p, pageable);
        List<CommentResponse> responses = comments.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, comments.getTotalElements());
    }

    @Override
    @Transactional
    public CommentResponse add(UUID postId, String userUuid, String content, String actorId) {
        Post p = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        UserEntity u = null;
        if (userUuid != null) {
            u = userRepository.findByUserUuid(userUuid)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Comment c = new Comment();
        c.setPost(p);
        c.setActorId(actorId);
        c.setUser(u);
        c.setComment(content);
        Comment saved = commentRepository.save(c);
        return toResponse(saved);
    }

    @Override
    public void delete(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    private CommentResponse toResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPost().getId());
        response.setComment(comment.getComment());
        if (comment.getCreatedAt() != null) {
            response.setCreatedAt(LocalDateTime.ofInstant(comment.getCreatedAt(), ZoneId.systemDefault()));
        }
        if (comment.getUpdatedAt() != null) {
            response.setUpdatedAt(LocalDateTime.ofInstant(comment.getUpdatedAt(), ZoneId.systemDefault()));
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
