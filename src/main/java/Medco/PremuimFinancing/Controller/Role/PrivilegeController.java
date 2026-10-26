package Medco.PremuimFinancing.Controller.Role;

import Medco.PremuimFinancing.Dto.Request.Role.PrivilegeRequest;
import Medco.PremuimFinancing.Dto.Response.MessageResponse;
import Medco.PremuimFinancing.Dto.Response.Role.PrivilegeMyResponse;
import Medco.PremuimFinancing.Dto.Response.Role.PrivilegeResponse;
import Medco.PremuimFinancing.Service.User.PrivilegeService;
import Medco.PremuimFinancing.ServiceImpl.User.UserDetailsImpl;
import Medco.PremuimFinancing.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/sebna/privilege")
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @PostMapping("/createPrivilege")
   @PreAuthorize("hasAuthority('ROLE_create_privilege')")
    public PrivilegeResponse createPrivilege(@Valid @RequestBody PrivilegeRequest privilegeRequest) {
        return privilegeService.createPrivilege(privilegeRequest);

    }


    @GetMapping(path="/{privilegeUuid}")
    @PreAuthorize("hasAuthority('ROLE_read_privilege')")
    public PrivilegeResponse getRole(@PathVariable String privilegeUuid) {
        return privilegeService.getPrivilege(privilegeUuid);
    }

    @GetMapping("/list")
//    @PreAuthorize("hasRole('ROLE_read_privileges')")
    public PrivilegeMyResponse getPrivileges(@RequestParam(name="search", required=false) String searchKey,
                                             @RequestParam(value="page", defaultValue = "1") int page,
                                             @RequestParam(value="limit", defaultValue = "500") int limit) {
//        UserDetails userDetails= SecurityUtils.getAuthenticatedUser();


        return privilegeService.getPrivileges(page, limit, searchKey);
    }

    @PutMapping(path="/{privilegeUuid}")
    @PreAuthorize("hasAuthority('ROLE_update_privilege')")
    public ResponseEntity<MessageResponse> updatePrivilege(@PathVariable String privilegeUuid, @Valid @RequestBody PrivilegeRequest privilegeRequest) {
        return privilegeService.updatePrivilege(privilegeUuid, privilegeRequest);

    }

    @DeleteMapping(path="/{privilegeUuid}")
    @PreAuthorize("hasAuthority('ROLE_delete_privilege')")
    public ResponseEntity<?> deletePrivilege(@PathVariable String privilegeUuid) {
        return privilegeService.deletePrivilege(privilegeUuid);

    }
}

