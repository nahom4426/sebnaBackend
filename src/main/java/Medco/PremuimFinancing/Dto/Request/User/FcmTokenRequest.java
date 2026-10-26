package Medco.PremuimFinancing.Dto.Request.User;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class FcmTokenRequest {
    @Column(nullable = false,unique = true)

    private String fcm_token;
    private boolean is_active=true;
}
