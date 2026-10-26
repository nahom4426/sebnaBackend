package Medco.PremuimFinancing.Controller.User;

import Medco.PremuimFinancing.Dto.Request.Password.ChangePasswordRequest;
import Medco.PremuimFinancing.Dto.Request.Password.ResetPasswordRequest;
import Medco.PremuimFinancing.Dto.Request.User.AuthRequest;
import Medco.PremuimFinancing.Dto.Request.User.SignUpRequest;
import Medco.PremuimFinancing.Dto.Request.User.UserRequest;
import Medco.PremuimFinancing.Dto.Response.MessageResponse;
import Medco.PremuimFinancing.Dto.Response.ProfilePcrure.ProfileResponse;
import Medco.PremuimFinancing.Dto.Response.User.ClientMyResponse;
import Medco.PremuimFinancing.Dto.Response.User.MyShareStatsResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserMyResponse;
import Medco.PremuimFinancing.Dto.Response.User.UserResponse;
import Medco.PremuimFinancing.Service.User.UserService;
import Medco.PremuimFinancing.utils.Pagination.PaginationUtils;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/sebna/users")
public class UserController {

    AuthenticationManager authenticationManager;
    UserService userService;

    public UserController(AuthenticationManager authenticationManager, UserService userService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/EnterVerificationCode")
    public ResponseEntity<?> verifyCode(@RequestParam String email,
                                        @RequestParam String code) {
        return userService.verifyCode(email, code);
    }

    @PutMapping("/update/{userUuid}")
    @PreAuthorize("hasAuthority('ROLE_update_user')")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable String userUuid, @RequestBody UserRequest userRequest) {
        return userService.updateUser(userUuid, userRequest);

    }

    @GetMapping("/{userUuid}")
    @PreAuthorize("hasAuthority('ROLE_read_user')")
    public UserResponse getUser(@PathVariable String userUuid) {
        return userService.getUser(userUuid);

    }

    @GetMapping("/me/share-stats")
    @PreAuthorize("isAuthenticated()")
    public MyShareStatsResponse getMyShareStats() {
        return userService.getMyShareStats();
    }

    @DeleteMapping("/delete/{userUuid}")
    @PreAuthorize("hasAuthority('ROLE_delete_user')")
    public ResponseEntity<?> deleteUser(@PathVariable String userUuid) {
        return userService.deleteUser(userUuid);

    }

    @GetMapping("/payerUsers")
    @PreAuthorize("hasAuthority('ROLE_read_payer-users')")
    public List<UserResponse> getPayerUsers(@RequestParam(value = "search") String search,
                                            @RequestParam(value="page", defaultValue = "1") int page,
                                            @RequestParam(value="limit", defaultValue = "25") int limit) {
        return userService.getPayerUsers(page,limit);

    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_read_users')")
    public UserMyResponse getUsers(
            @RequestParam(value = "search",required = false)String search,
            @RequestParam(value="page", defaultValue = "1") int page,
            @RequestParam(value="limit", defaultValue = "25") int size) {
        Pageable pageable = PaginationUtils.paginateResource(page,size,"id","desc");
        return userService.getUsers(search,pageable);
    }

    @GetMapping("/clients")
    @PreAuthorize("hasAuthority('ROLE_read_users')")
    public ClientMyResponse getClients(
            @RequestParam(value = "search",required = false) String search,
            @RequestParam(value="page", defaultValue = "1") int page,
            @RequestParam(value="limit", defaultValue = "25") int limit) {
        Pageable pageable = PaginationUtils.paginateResource(page,limit,"id","desc");
        return userService.getClients(search,pageable);
    }

    @GetMapping( "/search")
    @PreAuthorize("hasAuthority('ROLE_read_users')")
    public List<UserResponse> searchUsers(@RequestParam("search") String searchKey, @RequestParam(value="page", defaultValue = "1") int page,
                                          @RequestParam(value="limit", defaultValue = "25") int limit){
        return userService.searchUsers(searchKey,page,limit);

    }




    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordDetail){
        return userService.resetPassword(resetPasswordDetail);
    }


    @PutMapping( "/changePassword/{userUuid}")
//    @PreAuthorize("hasAuthority('ROLE_change_password')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
                                            @PathVariable String userUuid) {
        return userService.changePassword(changePasswordRequest, userUuid);

    }


    @GetMapping( "/email/verification/{emailVerificationToken}")
//    @PreAuthorize("hasAuthority('Email-Verification')")
    public ResponseEntity<?> verifyAccount(@PathVariable String emailVerificationToken) {
        return userService.verifyAccount(emailVerificationToken);

    }


//    @PutMapping( "/email/verification/resend/{email}")
//    @PreAuthorize("hasAuthority('Email-Verification')")
//    public ResponseEntity<?> reSendVerification(@PathVariable String email)
//            throws AddressException, MessagingException, IOException {
//        return userService.reSendVerification(email);
//
//    }

    @PutMapping( "/password/sendResetCode/{email}")
    public ResponseEntity<?> resetPassword(@PathVariable String email)
            throws  MessagingException {
        return userService.sendPasswordResetCode(email);

    }

    @PostMapping( "/password/checkResetCode")
    public ResponseEntity<?> checkResetCode(@RequestParam String verificationCode,
                                            @RequestParam String email) {
        return userService.checkResetCode(verificationCode, email);

    }

    @PutMapping(value = "/editProfile/{userUuid}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?>editProfile(@PathVariable String userUuid,
                                        @RequestParam String userName,
                                        @Valid @RequestBody(required = false) MultipartFile profilePicture) throws IOException {



        return userService.editProfile(userUuid,userName,profilePicture);

    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestParam("email")String email) {

            return userService.generateAndSendOtp(email);
    }


    @PostMapping("/sendSms/{userUuid}")
    public ResponseEntity<?> sendSms(@PathVariable("userUuid")String userUuid,
                                     @RequestParam("message") String message) {

            return userService.generateAndSendSms(userUuid,message);


    }


    @GetMapping("/getProfile/{userUuid}")
    @PreAuthorize("hasAuthority('ROLE_read_user')")
    public ProfileResponse getProfile(@PathVariable String userUuid)
    {

        return userService.getProfile(userUuid);
    }

    @PostMapping("/admin/resend-welcome-password/{userUuid}")
    @PreAuthorize("hasAuthority('ROLE_update_user')")
    public ResponseEntity<?> adminResendWelcomePasswordEmail(@PathVariable String userUuid) {
        return userService.adminRetryWelcomePasswordEmail(userUuid);
    }

    @PostMapping("/admin/reset-password/{userUuid}")
    @PreAuthorize("hasAuthority('ROLE_update_user')")
    public ResponseEntity<?> adminResetUserPassword(@PathVariable String userUuid) {
        return userService.adminResetUserPassword(userUuid);
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType == null || (!contentType.equals("image/jpeg") &&
                !contentType.equals("image/png") &&
                !contentType.equals("image/gif"));
    }

}