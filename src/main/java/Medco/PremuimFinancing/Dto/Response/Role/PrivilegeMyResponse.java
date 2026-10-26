package Medco.PremuimFinancing.Dto.Response.Role;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PrivilegeMyResponse {
    private long totalPages;
    private List<PrivilegeResponse> response;
}
