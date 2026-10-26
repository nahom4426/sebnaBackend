package Medco.PremuimFinancing.Dto.Response.Role;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleMyResponse {
   private long totalPages;
   private List<RoleResponse> response;
}
