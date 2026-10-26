package Medco.PremuimFinancing.Service.User;

import Medco.PremuimFinancing.Dto.Request.User.FcmTokenRequest;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface FcmTokenService {
    ResponseEntity<?> save(String userUuid, FcmTokenRequest tokenData);
    void sendNotification(String targetToken,String title, String notificationData, Map<String, String> customData);
}
