package Medco.PremuimFinancing.Dto.Request.Institution;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class InstitutionCreateRequest {
    @NotBlank(message = "Institution name is required")
    @Size(min = 2, max = 150, message = "Institution name must be between 2 and 150 characters")
    private String name;

//    @Schema(type = "string", format = "binary")  // This tells Swagger that it's a file
//    private MultipartFile logo; // Optional - Image file (PNG, JPEG, JPG, GIF, or WEBP)

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 255, message = "Website must not exceed 255 characters")
    private String website;
}
