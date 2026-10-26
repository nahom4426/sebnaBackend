package Medco.PremuimFinancing.Service;

import Medco.PremuimFinancing.Entity.User.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;

public interface MessagingService {
    ResponseEntity<?> sendOtp(UserEntity phoneNumber, String otp);

    ResponseEntity<?> sendSms(UserEntity user, String message);
}
