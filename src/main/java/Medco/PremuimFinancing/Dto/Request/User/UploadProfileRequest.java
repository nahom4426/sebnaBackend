package Medco.PremuimFinancing.Dto.Request.User;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadProfileRequest {
    private MultipartFile profilePicture;
    private String userUuid;
}
