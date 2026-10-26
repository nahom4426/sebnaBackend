package Medco.PremuimFinancing.Service.User;

import Medco.PremuimFinancing.Dto.Request.Role.PrivilegeRequest;
import Medco.PremuimFinancing.Dto.Response.MessageResponse;
import Medco.PremuimFinancing.Dto.Response.Role.PrivilegeMyResponse;
import Medco.PremuimFinancing.Dto.Response.Role.PrivilegeResponse;
import org.springframework.http.ResponseEntity;



public interface PrivilegeService {
    PrivilegeResponse createPrivilege(PrivilegeRequest privilegeRequest);
    ResponseEntity<MessageResponse> updatePrivilege(String privilegeUuid, PrivilegeRequest privilegeRequest);
    ResponseEntity<?> deletePrivilege(String privilegeUuid);
    PrivilegeResponse getPrivilege(String privilegeUuid);
    PrivilegeMyResponse getPrivileges(int page, int limit, String searchKey);

}

