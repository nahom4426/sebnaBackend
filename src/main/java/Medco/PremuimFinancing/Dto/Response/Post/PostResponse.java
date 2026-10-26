package Medco.PremuimFinancing.Dto.Response.Post;

import Medco.PremuimFinancing.Dto.Response.Comment.CommentResponse;
import Medco.PremuimFinancing.Dto.Response.PostLike.PostLikeResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserResponse;
import Medco.PremuimFinancing.Enums.Post.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private UUID id;
    private UserResponse user;
    private String title;
    private String content;
    private PostCategory category;
    private Long views;
    private String imageBase64;
    private Long likeCount;
    private Long commentCount;
    private Boolean userLiked;
    private List<CommentResponse> comments;
    private List<PostLikeResponse> likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
