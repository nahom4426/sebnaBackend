package Medco.PremuimFinancing.Dto.Response.Role;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleResponse {
    private String roleName;
    private String roleDescription;
    private String roleUuid;
    private List<PrivilegeResponse> privileges;
}
