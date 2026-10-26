package Medco.PremuimFinancing.Controller.Institution;

import Medco.PremuimFinancing.Service.Institution.InstitutionMembershipService;
import Medco.PremuimFinancing.Repository.Institution.UserInstitutionRepository;
import Medco.PremuimFinancing.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sebna/institution-memberships")
public class InstitutionMembershipController {

    private final InstitutionMembershipService membershipService;
    private final UserInstitutionRepository userInstitutionRepository;

    public InstitutionMembershipController(InstitutionMembershipService membershipService, UserInstitutionRepository userInstitutionRepository) {
        this.membershipService = membershipService;
        this.userInstitutionRepository = userInstitutionRepository;
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('ROLE_update_user')")
    public Map<String, Object> assign(@RequestParam UUID institutionId, @RequestParam String userUuid) {
        membershipService.assignUser(institutionId, userUuid);
        return Map.of("message", "User assigned to institution");
    }

    @DeleteMapping("/unassign")
    @PreAuthorize("hasAuthority('ROLE_update_user')")
    public Map<String, Object> unassign(@RequestParam UUID institutionId, @RequestParam String userUuid) {
        membershipService.unassignUser(institutionId, userUuid);
        return Map.of("message", "User unassigned from institution");
    }

    @GetMapping("/my")
    public Map<String, Object> myInstitution() {
        var auth = SecurityUtils.getAuthenticatedUser();
        var ui = userInstitutionRepository.findByUser_UserUuid(auth.getUserUuid()).orElse(null);

        Map<String, Object> result = new HashMap<>();
        result.put("userUuid", auth.getUserUuid());
        result.put("institutionId", ui == null ? null : ui.getInstitution().getId());
        result.put("institutionName", ui == null ? null : ui.getInstitution().getName());
        return result;
    }
}
