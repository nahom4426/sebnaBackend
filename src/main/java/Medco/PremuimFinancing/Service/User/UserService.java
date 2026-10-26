package Medco.PremuimFinancing.Service.User;


import Medco.PremuimFinancing.Dto.Request.Password.ChangePasswordRequest;
import Medco.PremuimFinancing.Dto.Request.Password.ResetPasswordRequest;
import Medco.PremuimFinancing.Dto.Request.User.AuthRequest;
import Medco.PremuimFinancing.Dto.Request.User.SignUpRequest;
import Medco.PremuimFinancing.Dto.Request.User.SebnaSignUpRequest;
import Medco.PremuimFinancing.Dto.Request.User.UserRequest;
import Medco.PremuimFinancing.Dto.Response.MessageResponse;
import Medco.PremuimFinancing.Dto.Response.ProfilePcrure.ProfileResponse;
import Medco.PremuimFinancing.Dto.Response.User.ClientResponse;
import Medco.PremuimFinancing.Dto.Response.User.ClientMyResponse;
import Medco.PremuimFinancing.Dto.Response.User.MyShareStatsResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserMyResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    ResponseEntity<?> AuthenticateUser(AuthRequest authRequest);

    ResponseEntity<?> Save(SignUpRequest signUpRequest);

    ResponseEntity<?> sebnaSignUp(SebnaSignUpRequest signUpRequest);

    ResponseEntity<MessageResponse> updateUser(String userUuid, UserRequest userRequest);

    ResponseEntity<?> deleteUser(String userUuid);

    UserResponse getUser(String userUuid);

    ResponseEntity<?> sendPasswordResetCode(String email);

    UserMyResponse getUsers(String search,Pageable pageable);

    List<UserResponse> getPayerUsers(int page, int limit);

    ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordDetail);

    ResponseEntity<?> changePassword(ChangePasswordRequest resetPasswordDetail, String userUuid);

    ResponseEntity<?> verifyAccount(String emailVerificationToken);

    ResponseEntity<?> checkResetCode(String verificationCode,String email);

    List<UserResponse> searchUsers(String searchKey, int page, int limit);

    ResponseEntity<?> editProfile(String userUuid, String userName, MultipartFile profilePicture) throws IOException;

    ProfileResponse getProfile(String userUuid);

    ClientMyResponse getClients(String search, Pageable pageable);

    ResponseEntity<?> verifyCode(String phoneNumber, String code);

    ResponseEntity<?> generateAndSendOtp(String phoneNumber);

    ResponseEntity<?> generateAndSendSms(String userUuid,String message);

    ResponseEntity<?> adminRetryWelcomePasswordEmail(String userUuid);

    ResponseEntity<?> adminResetUserPassword(String userUuid);

    MyShareStatsResponse getMyShareStats();
}
