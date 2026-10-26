package Medco.PremuimFinancing.Controller.User;

import Medco.PremuimFinancing.Dto.Request.User.FcmTokenRequest;
import Medco.PremuimFinancing.Service.User.FcmTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sebna/fcmToken")
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    public FcmTokenController(FcmTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }
    @PostMapping("/register/{userUuid}")
    public ResponseEntity<?> registerToken(@PathVariable String userUuid,
                                           @RequestBody FcmTokenRequest tokenData) {

        return fcmTokenService.save(userUuid,tokenData);

    }
}
