package Medco.PremuimFinancing.ServiceImpl;
import Medco.PremuimFinancing.Dto.Request.Role.RoleRequest;
import Medco.PremuimFinancing.Dto.Request.Role.RoleUpdateRequest;
import Medco.PremuimFinancing.Dto.Response.MessageResponse;
import Medco.PremuimFinancing.Dto.Response.Role.PrivilegeResponse;
import Medco.PremuimFinancing.Dto.Response.Role.RoleResponse;
import Medco.PremuimFinancing.Entity.UserRole.Privilege;
import Medco.PremuimFinancing.Entity.UserRole.Role;
import Medco.PremuimFinancing.Repository.Role.PrivilegeRepository;
import Medco.PremuimFinancing.Repository.Role.RoleRepository;
import Medco.PremuimFinancing.Service.User.RoleService;
import Medco.PremuimFinancing.exception.BadRequestException;
import Medco.PremuimFinancing.exception.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final PrivilegeRepository privilegeRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }


    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {

        if (roleRepository.existsByRoleName(roleRequest.getRoleName())) {
            throw new BadRequestException("Error: Role Name is already registered!");
        }
        var newRole = new Role();

        newRole.setRoleDescription(roleRequest.getRoleDescription());
        newRole.setRoleName(roleRequest.getRoleName().toLowerCase());


        Set<String> privilegeUuid = roleRequest.getPrivileges();

        List<PrivilegeResponse>privilegeResponseList=new ArrayList<>();
        if (privilegeUuid != null && !privilegeUuid.isEmpty()) {
            Set<Privilege> privileges = privilegeRepository.findByPrivilegeUuidIn(privilegeUuid);
            newRole.setPrivileges(privileges);
            for (Privilege privilege:privileges){
                PrivilegeResponse privilegeResponse=new PrivilegeResponse();
                BeanUtils.copyProperties(privilege,privilegeResponse);
                privilegeResponseList.add(privilegeResponse);
            }
        }

        roleRepository.save(newRole);

        // Build RoleResponse
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setRoleName(newRole.getRoleName());
        roleResponse.setRoleDescription(newRole.getRoleDescription());
        roleResponse.setRoleUuid(newRole.getRoleUuid()); // Assuming Role entity has a getRoleUuid() method

        roleResponse.setPrivileges(privilegeResponseList);

        return roleResponse;
    }



    @Override
    public ResponseEntity<?> updateRole(String roleUuid, RoleUpdateRequest roleUpdateRequest) {

        Role role=roleRepository.findByRoleUuid(roleUuid);
        role.setRoleDescription(roleUpdateRequest.getRoleDescription());
        role.setRoleName(roleUpdateRequest.getRoleName());

        String[] privilegeUuid =  roleUpdateRequest.getPrivileges();
        if (privilegeUuid != null ) {
            Set<Privilege> privileges = new HashSet<>();
            for (String privilegeUid:privilegeUuid){
                privileges.add(privilegeRepository.findByPrivilegeUuid(privilegeUid));
            }
            if (privileges.isEmpty())throw new ResourceNotFoundException("Privileges list is empty");
            role.setPrivileges(privileges);
        }
        roleRepository.save(role);
        return ResponseEntity.ok("role updated successfully");



    }

    @Override
    public ResponseEntity<?> deleteRole(String roleUuid) {
        Role role = roleRepository.findByRoleUuid(roleUuid);
        if (role == null)
            throw new RuntimeException("Role not found.");
        role.getPrivileges().clear();
        roleRepository.save(role);

        roleRepository.delete(role);

        return ResponseEntity.ok(new MessageResponse("Role soft deleted permanently!"));
    }

    @Override
    public RoleResponse getRole(String roleUuid) {
        Optional<Role> role = Optional.ofNullable(roleRepository.findByRoleUuid(roleUuid));

        if(role.isEmpty())
            throw new BadRequestException("Can't Find Role With The Provided id");

        var response = new RoleResponse();
        Set<Privilege> privilege = role.get().getPrivileges();
        List<PrivilegeResponse> privilegeResponseList=privilege.stream().map(privilege1 -> {
            PrivilegeResponse privilegeResponse=new PrivilegeResponse();
            BeanUtils.copyProperties(privilege1,privilegeResponse);
            return privilegeResponse;
        }).toList();
       // List<String> rolePrivilege = privilegeRepository.findPrivilegeNamesByRoleUuid(roleUuid);
        BeanUtils.copyProperties(role.get(), response);
        response.setPrivileges(privilegeResponseList);

        return response;
    }




//
//    @Override
//    public ResponseEntity<?> deleteRolePrivileges(String roleUuid, AddRolePrivilegesRequest rolePrivilegesRequest) {
//
//        return null;
//    }

    @Override
    public List<RoleResponse> searchRoles(String searchTerm, int page, int limit) {
        if (page > 0) page = page - 1;

        Pageable pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());

        Page<Role> rolesPage = roleRepository.findByRoleNameContainingIgnoreCaseOrRoleDescriptionContainingIgnoreCase(searchTerm, searchTerm, pageRequest);



        List<Role> roleList = rolesPage.getContent();
        List<RoleResponse> roleResponse = new ArrayList<>();
        for (Role r : roleList) {
            RoleResponse rr = new RoleResponse();
            if (roleResponse.isEmpty())
               // rr.setTotalPages(totalPages);
              BeanUtils.copyProperties(r, rr);
            roleResponse.add(rr);
        }

        return roleResponse;
    }

    @Override
    public RoleResponse getRoleName(String name) {
       Role role = roleRepository.findByRoleName(name).orElseThrow(()->new BadRequestException("Can't Find Role With The Provided name"));
//

        var response = new RoleResponse();
        BeanUtils.copyProperties(role, response);

        return response;
    }

    @Override
    public Page<Role> findAllRoles(String search, Pageable pageable) {
        return search != null
                ? roleRepository.findByRoleNameContainingIgnoreCase(search, pageable)
                : roleRepository.findAll(pageable);
    }




}


