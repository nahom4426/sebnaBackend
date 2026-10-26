package Medco.PremuimFinancing.Service.User;

import Medco.PremuimFinancing.Dto.Request.Role.RoleRequest;
import Medco.PremuimFinancing.Dto.Request.Role.RoleUpdateRequest;
import Medco.PremuimFinancing.Dto.Response.Role.RoleResponse;
import Medco.PremuimFinancing.Entity.UserRole.Role;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest roleRequest);
    ResponseEntity<?> updateRole(String roleUuid, @Valid RoleUpdateRequest roleUpdateRequest);
    ResponseEntity<?> deleteRole(String roleString);
    RoleResponse getRole(String roleUuid);
//    ResponseEntity<?> deleteRolePrivileges(String roleUuid, @Valid AddRolePrivilegesRequest rolePrivilegesRequest);
    List<RoleResponse> searchRoles(String searchKey, int page, int limit);

    RoleResponse getRoleName(String name);

    Page<Role> findAllRoles(String search, Pageable pageable);
}
