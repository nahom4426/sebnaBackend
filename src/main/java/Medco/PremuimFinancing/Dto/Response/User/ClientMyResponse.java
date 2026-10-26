package Medco.PremuimFinancing.Dto.Response.User;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ClientMyResponse {
    private long totalPages;
    private long totalElements;
    private int pageNumber;
    private List<ClientResponse> response;
}
