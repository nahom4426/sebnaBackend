package Medco.PremuimFinancing.ServiceImpl;

import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.MessagingService;
import Medco.PremuimFinancing.exception.BadRequestException;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;

@Service
public class MessagingServiceImpl implements MessagingService {
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @Value("${afromessage.api.url}")
    private String apiUrl;

    @Value("${afromessage.api.token}")
    private String apiToken;

    @Value("${afromessage.api.identifierId}")
    private String identifierId;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${mail.custom.sender.name}")
    private String customSenderName;

    private final OkHttpClient client;

    public MessagingServiceImpl(UserRepository userRepository, OkHttpClient client, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.client = client;
        this.javaMailSender = javaMailSender;
    }

    public ResponseEntity<?> sendOtp(UserEntity user, String otp) {
        System.out.println("we are in the send otp "+ otp);

        String messageContent = "Your OTP is: " + otp;

        return sendEmailOtp(user.getEmail(), messageContent, otp);

    }

    @Override
    public ResponseEntity<?> sendSms(UserEntity user, String message) {

        return send(user.getMobilePhone(),message);

    }

    private ResponseEntity<?> send(String mobilePhone, String message) {
        HttpUrl url = HttpUrl.parse(apiUrl).newBuilder()
                .addQueryParameter("to",  mobilePhone)
                .addQueryParameter("message", message)
                .addQueryParameter("callback", "")
                .addQueryParameter("from", identifierId)
//                .addQueryParameter("sender", )
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiToken)
                .build();

//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//
//                throw new BadRequestException( "Error on sending otp: " + response.message());
//            }
//
//            return ResponseEntity.ok("Success: " + response.body().string());
//        } catch (IOException e) {
//            throw new BadRequestException("Request failed: " + e.getMessage());
//        }
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                throw new BadRequestException(
                        "Error sending OTP. Status code: " + response.code() + ", body: " + responseBody
                );
            }

            return ResponseEntity.ok("Success: " + responseBody);
        } catch (IOException e) {
            throw new BadRequestException("Request failed: " + e.getMessage());
        }

    }


    private ResponseEntity<?> sendEmailOtp(String email, String messageContent, String otp) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(mailUsername, customSenderName);
            helper.setTo(email);
            helper.setSubject("Your OTP Verification Code");
            helper.setText("<html><body>" +
                    "<h2>Verification Code</h2>" +
                    "<p>" + messageContent + "</p>" +
                    "<p>This code will expire in 10 minutes.</p>" +
                    "</body></html>", true);

            javaMailSender.send(message);
            return ResponseEntity.ok("OTP sent successfully to email");
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            throw new BadRequestException("Error sending OTP email: " + e.getMessage());
        }
    }

}