package Medco.PremuimFinancing.Dto.Request.Institution;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InstitutionRequest {
    @NotBlank(message = "Institution name is required")
    @Size(min = 2, max = 150, message = "Institution name must be between 2 and 150 characters")
    private String name;

    @NotBlank(message = "Logo is required")
    @Pattern(regexp = "^data:image/(png|jpeg|jpg|gif|webp);base64,[A-Za-z0-9+/=]+$", 
             message = "Logo must be a valid base64 encoded image (PNG, JPEG, JPG, GIF, or WEBP)")
    private String logo;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}(/.*)?$", 
             message = "Website must be a valid URL (e.g., https://example.com or example.com)")
    private String website;
}
