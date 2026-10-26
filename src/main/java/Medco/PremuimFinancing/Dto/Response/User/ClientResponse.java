package Medco.PremuimFinancing.Dto.Response.User;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ClientResponse {
    private String userUuid;
    private String clientName;
    private Date registrationDate;
    private String vehicleDetail;
    private String phoneNumber;

}
