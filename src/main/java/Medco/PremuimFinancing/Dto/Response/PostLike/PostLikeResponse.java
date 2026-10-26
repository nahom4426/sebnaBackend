package Medco.PremuimFinancing.Dto.Response.PostLike;

import Medco.PremuimFinancing.Dto.Response.User.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeResponse {
    private UUID id;
    private UUID postId;
    private UserResponse user;
    private LocalDateTime createdAt;
}
