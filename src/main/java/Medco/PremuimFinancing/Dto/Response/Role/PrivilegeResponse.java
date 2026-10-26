package Medco.PremuimFinancing.Dto.Response.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeResponse {
    private String privilegeName;
    private String privilegeDescription;
    private String privilegeCategory;
    private String privilegeUuid;

}
