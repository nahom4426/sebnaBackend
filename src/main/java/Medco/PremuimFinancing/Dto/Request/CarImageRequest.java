package Medco.PremuimFinancing.Dto.Request;

import Medco.PremuimFinancing.Enums.Image.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class CarImageRequest {
    private String carUuid;
    private String imageName;
//    private String imagePath;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Schema(type = "string", format = "binary")  // This tells Swagger that it's a file
    private MultipartFile image;

}
