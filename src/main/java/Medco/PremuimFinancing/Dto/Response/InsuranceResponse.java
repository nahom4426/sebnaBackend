package Medco.PremuimFinancing.Dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


@Setter
@Getter
public class InsuranceResponse {
    private String insuranceUuid;
    private String insuranceName;
    private Date registrationDate;
    private String registeredBy;
    private Date updatedDate;
    private String updatedBy;
    private String accountNumber;
//    private MultipartFile image;
//    @Schema(hidden = true)
//    private String image;
    private byte[] profile;

}
