package Medco.PremuimFinancing.Dto.Request.Password;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    private String email;
    private String verificationCode;
    private String confirmPassword;
    private String newPassword;

}
