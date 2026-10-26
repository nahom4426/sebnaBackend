package Medco.PremuimFinancing.ServiceImpl.User;

import Medco.PremuimFinancing.Dto.Request.User.FcmTokenRequest;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Entity.User.UserFcmToken;
import Medco.PremuimFinancing.Repository.User.FcmTokenRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.User.FcmTokenService;
import Medco.PremuimFinancing.exception.BadRequestException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;




@Service
public class FcmTokenServiceImpl implements FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;


//    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
//    private static final String SERVER_KEY = "YOUR_FCM_SERVER_KEY";

    public FcmTokenServiceImpl(FcmTokenRepository fcmTokenRepository, UserRepository userRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<?> save(String userUuid, FcmTokenRequest tokenData) {
        UserEntity user= userRepository.findByUserUuid(userUuid).orElseThrow(()->new BadRequestException("user not found"));

       UserFcmToken existing = fcmTokenRepository.findByUser(user);
       UserFcmToken userFcmToken = fcmTokenRepository.findByUserUserUuid(userUuid);
       UserFcmToken fcmToken=new UserFcmToken();
       if (existing!=null){
           fcmToken=existing;
           fcmToken.setFcm_token(tokenData.getFcm_token());
       }else {

           fcmToken.setFcm_token(tokenData.getFcm_token());
           fcmToken.setUser(user);
           fcmToken.setCreated_at(new Date());
       }
        fcmTokenRepository.save(fcmToken);
        System.out.println("in the fcm token "+tokenData.getFcm_token());

        return ResponseEntity.ok("Token saved");

    }

    public void sendNotification(String targetToken,String title, String notificationData, Map<String, String> customData) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "key=" + SERVER_KEY);
//
//        Map<String, Object> message = new HashMap<>();
//        message.put("to", targetToken);
//
//        if (notificationData != null) {
//            message.put("notification", notificationData); // for foreground push (shown in notification bar)
//        }
//
//        if (customData != null) {
//            message.put("data", customData); // for background push or extra data
//        }
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
//        ResponseEntity<String> response = restTemplate.postForEntity(FCM_API_URL, request, String.class);
//        System.out.println("FCM response: " + response.getBody());


        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(targetToken);

            // If title and body are provided, attach a notification
            if (title != null || notificationData != null) {
                Notification notification = Notification.builder()
                        .setTitle(title)
                        .setBody(notificationData)
                        .build();
                messageBuilder.setNotification(notification);
            }

            // Attach data payload if present
            if (customData != null && !customData.isEmpty()) {
                messageBuilder.putAllData(customData);
            }

            Message message = messageBuilder.build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Successfully sent message: " + response);

        } catch (FirebaseMessagingException e) {
            System.err.println("❌ Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
