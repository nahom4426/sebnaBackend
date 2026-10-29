package Medco.PremuimFinancing.utils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendNotifications {
    public void sendNotification(String targetToken,String title, String notificationData, Map<String, String> customData) {

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

            if (FirebaseApp.getApps().isEmpty()) {
                System.err.println("⚠️ Firebase is not configured - notification not sent");
                return;
            }
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Successfully sent message: " + response);

        } catch (FirebaseMessagingException e) {
            System.err.println("❌ Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
