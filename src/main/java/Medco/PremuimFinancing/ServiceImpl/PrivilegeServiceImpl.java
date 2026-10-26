package Medco.PremuimFinancing.ServiceImpl;

import Medco.PremuimFinancing.Dto.Request.Role.PrivilegeRequest;
import Medco.PremuimFinancing.Dto.Response.MessageResponse;
import Medco.PremuimFinancing.Dto.Response.Role.PrivilegeMyResponse;
import Medco.PremuimFinancing.Dto.Response.Role.PrivilegeResponse;
import Medco.PremuimFinancing.Entity.UserRole.Privilege;
import Medco.PremuimFinancing.Repository.Role.PrivilegeRepository;
import Medco.PremuimFinancing.Service.User.PrivilegeService;
import Medco.PremuimFinancing.exception.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public PrivilegeResponse createPrivilege(PrivilegeRequest privilege) {

        PrivilegeResponse newResponse = new PrivilegeResponse();

        if (privilegeRepository.existsByPrivilegeName(privilege.getPrivilegeName())) throw new BadRequestException("privilege is already registered!");

        Privilege privilege1 = new Privilege();
        BeanUtils.copyProperties(privilege, privilege1);
        privilege1.setPrivilegeName(privilege.getPrivilegeName().toLowerCase());
        privilegeRepository.save(privilege1);

        BeanUtils.copyProperties(privilege1,newResponse);
        return newResponse;

    }


    @Override
    public ResponseEntity<MessageResponse> updatePrivilege(String privilegeUuid, PrivilegeRequest privilegeRequest) {

        Privilege privilege = privilegeRepository.findByPrivilegeUuid(privilegeUuid);
        List<Privilege> privilegeName = privilegeRepository.findAllByPrivilegeName(privilegeRequest.getPrivilegeName());
        List<Privilege> privileges = privilegeRepository.findAllByPrivilegeDescription(privilegeRequest.getPrivilegeDescription());

        if(privilege == null)
            throw new RuntimeException("Privilege not found.");
        if (privileges.size() >=2 || privilegeName.size() >=2)
            throw new RuntimeException("Privilege  name or description is exist. Update privilege to have unique name and description");

        privilege.setPrivilegeName(privilegeRequest.getPrivilegeName());
        privilege.setPrivilegeDescription(privilegeRequest.getPrivilegeDescription());
        privilege.setPrivilegeCategory(privilegeRequest.getPrivilegeCategory());

        privilegeRepository.save(privilege);

        return ResponseEntity.ok(new MessageResponse("Privilege Updated successfully!"));
    }

    @Override
    public ResponseEntity<?> deletePrivilege(String privilegeString) {

        Privilege privilege = privilegeRepository.findByPrivilegeUuid(privilegeString);
        if(privilege == null)
            throw new RuntimeException("Privilege not found.");
        privilegeRepository.delete(privilege);
        return ResponseEntity.ok(new MessageResponse("Privilege soft deleted permanently!"));
    }

    @Override
    public PrivilegeResponse getPrivilege(String privilegeUuid) {
        Privilege privilege  = privilegeRepository.findByPrivilegeUuid(privilegeUuid);
        if(privilege == null)
            throw new RuntimeException("PrivilegeUuid not found.");
        PrivilegeResponse privilegeREsponse = new PrivilegeResponse();
        privilegeREsponse.setPrivilegeUuid(privilege.getPrivilegeUuid());
        privilegeREsponse.setPrivilegeName(privilege.getPrivilegeName());
        privilegeREsponse.setPrivilegeDescription(privilege.getPrivilegeDescription());
        return privilegeREsponse;
    }


    @Override
    public PrivilegeMyResponse getPrivileges(int page, int limit, String searchKey) {
        if (page > 0) page = page - 1;

        Pageable pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<Privilege> privilegePage = (searchKey != null && !searchKey.isBlank())
                ? privilegeRepository.findByPrivilegeNameContainingIgnoreCaseOrPrivilegeDescriptionContainingIgnoreCaseOrPrivilegeCategoryContainingIgnoreCase(searchKey, searchKey, searchKey, pageRequest)
                : privilegeRepository.findAll(pageRequest);
        long totalPages = privilegePage.getTotalPages();
        List<Privilege> privilegeList = privilegePage.getContent();

        PrivilegeMyResponse response = new PrivilegeMyResponse();
        response.setTotalPages(totalPages);
        List<PrivilegeResponse> privilegeResponseList = new ArrayList<>();
        for (Privilege privilege : privilegeList) {
            PrivilegeResponse privilegeResponse = new PrivilegeResponse();
            BeanUtils.copyProperties(privilege, privilegeResponse);
            privilegeResponseList.add(privilegeResponse);
        }

        response.setResponse(privilegeResponseList);
        return response;
    }


//    @Override
//    public List<PrivilegeResponse> searchPrivileges(String searchTerm, int page, int limit) {
//
//        if(page > 0) page = page - 1;
//
//        Pageable pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
//
//        Page<Privilege> PrivilegesPage = privilegeRepository.findByPrivilegeNameContainingOrPrivilegeDescriptionContainingOrPrivilegeCategoryContaining(searchTerm,searchTerm,searchTerm, pageRequest);
//
//
//
//        List<Privilege> privilegeList = PrivilegesPage.getContent();
//        List<PrivilegeResponse> privilegeResponse = new ArrayList<>();
//        for (Privilege p : privilegeList) {
//            PrivilegeResponse pr = new PrivilegeResponse();
//            BeanUtils.copyProperties(p, pr);
//            privilegeResponse.add(pr);
//        }
//
//        return privilegeResponse;
//    }

}
