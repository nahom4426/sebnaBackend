package Medco.PremuimFinancing.Dto.Response.Security;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginLogPageResponse {
    private long totalElements;
    private long totalPages;
    private int pageNumber;
    private List<LoginLogResponse> response;
}
