package Medco.PremuimFinancing.Controller.Settings;

import Medco.PremuimFinancing.Dto.Request.Settings.PricePerShareUpdateRequest;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Settings.AppSettingService;
import Medco.PremuimFinancing.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/sebna/settings")
public class SettingsController {

    private final AppSettingService appSettingService;
    private final UserRepository userRepository;

    public SettingsController(AppSettingService appSettingService, UserRepository userRepository) {
        this.appSettingService = appSettingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/price-per-share")
    @PreAuthorize("hasAuthority('ROLE_read_users')")
    public Map<String, BigDecimal> getPricePerShare() {
        return Map.of("pricePerShare", appSettingService.getPricePerShare());
    }

    @PutMapping("/price-per-share")
    @PreAuthorize("hasAuthority('ROLE_update_user')")
    public ResponseEntity<?> updatePricePerShare(@Valid @RequestBody PricePerShareUpdateRequest request) {
        var auth = SecurityUtils.getAuthenticatedUser();
        UserEntity actor = userRepository.findByUserUuid(auth.getUserUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        String roleName = actor.getRole() != null ? actor.getRole().getRoleName() : null;
        if (roleName == null || !("SuperAdmin".equalsIgnoreCase(roleName) || "super_admin".equalsIgnoreCase(roleName))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }

        BigDecimal updated = appSettingService.setPricePerShare(request.getPricePerShare());
        return ResponseEntity.ok(Map.of("pricePerShare", updated));
    }
}
