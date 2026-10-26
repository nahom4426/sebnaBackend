package Medco.PremuimFinancing.Controller.User;

import Medco.PremuimFinancing.Dto.Request.User.AuthRequest;
import Medco.PremuimFinancing.Dto.Request.User.SebnaSignUpRequest;
import Medco.PremuimFinancing.Service.User.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sebna/users")
public class SebnaUserController {

    private final UserService userService;

    public SebnaUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signUp")
    @PreAuthorize("hasAuthority('ROLE_create_user')")
    public ResponseEntity<?> signUp(@Valid @RequestBody SebnaSignUpRequest signUpRequest){
        return userService.sebnaSignUp(signUpRequest);
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody AuthRequest authRequest){
        return userService.AuthenticateUser(authRequest);
    }
}
