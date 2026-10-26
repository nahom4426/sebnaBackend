package Medco.PremuimFinancing.Dto.Request.Comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank
    private String comment;
}
