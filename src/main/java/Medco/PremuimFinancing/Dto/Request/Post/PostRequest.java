package Medco.PremuimFinancing.Dto.Request.Post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
