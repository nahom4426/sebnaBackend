package Medco.PremuimFinancing.Controller.Role;

import Medco.PremuimFinancing.Dto.Request.Role.RoleRequest;
import Medco.PremuimFinancing.Dto.Request.Role.RoleUpdateRequest;
import Medco.PremuimFinancing.Dto.Response.Role.RoleResponse;
import Medco.PremuimFinancing.Entity.UserRole.Role;
import Medco.PremuimFinancing.Service.User.RoleService;
import Medco.PremuimFinancing.utils.Pagination.PaginationUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/sebna/role")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {

        this.roleService = roleService;
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_create_role')")
    public RoleResponse createRole(@Valid @RequestBody RoleRequest roleRequest) {
        return roleService.createRole(roleRequest);
    }

    @PutMapping(path="/{roleUuid}")
    @PreAuthorize("hasAuthority('ROLE_update_role')")
    public ResponseEntity<?> updateRole(@PathVariable String roleUuid, @Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
        return roleService.updateRole(roleUuid, roleUpdateRequest);
    }


    @GetMapping(path="/{roleUuid}")
    @PreAuthorize("hasAuthority('ROLE_read_role')")
    public RoleResponse getRole(@PathVariable String roleUuid) {

        return roleService.getRole(roleUuid);
    }

    @GetMapping("/findByName/{name}")
    @PreAuthorize("hasAuthority('ROLE_read_role')")
    public RoleResponse getRoleByName(@PathVariable String name) {

        return roleService.getRoleName(name);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('ROLE_read_roles')")
    public Page<Role> findAllRoles(@RequestParam (required = false)String search,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit ){
        Pageable pageable = PaginationUtils.paginateResource(page,limit,"id","desc");
        return roleService.findAllRoles(search,pageable);
    }


    @DeleteMapping(path="/{roleUuid}")
    @PreAuthorize("hasAuthority('ROLE_delete_role')")
    public ResponseEntity<?> deleteRole(@PathVariable String roleUuid) {

        return roleService.deleteRole(roleUuid);

    }

//    @DeleteMapping("delete/privileges/{roleUuid}")
//    @PreAuthorize("hasRole('ROLE_delete_role')")
//    public ResponseEntity<?> deleteRolePrivileges(@PathVariable String roleUuid, @Valid @RequestBody AddRolePrivilegesRequest rolePrivilegesRequest) {
//        return roleService.deleteRolePrivileges(roleUuid, rolePrivilegesRequest);
//    }

    @GetMapping(path = "/search")
    @PreAuthorize("hasAuthority('ROLE_read_roles')")
    public List<RoleResponse> searchRoles(@RequestParam("search") String searchKey, @RequestParam(value="page", defaultValue = "1") int page,
                                          @RequestParam(value="limit", defaultValue = "5") int limit){
        return roleService.searchRoles(searchKey,page,limit);

    }
}

