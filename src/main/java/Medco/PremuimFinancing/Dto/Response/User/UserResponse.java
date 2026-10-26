package Medco.PremuimFinancing.Dto.Response.User;

import Medco.PremuimFinancing.utils.Audit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UserResponse extends Audit {

    private String userUuid;
    private String email;
    private String title;
    private String firstName;
    private String fatherName;
    private String grandFatherName;
    private String Gender;

    private String mobilePhone;
//    private Status userStatus;
//    private UserType userType;
    private String providerUuid;
    private String payerUuid;
    private String profilePicture;

    private boolean emailVerificationStatus;
    private boolean phoneVerificationStatus;
    private boolean isDeleted;
    private String roleUuid;
    private String roleName;

     private BigDecimal sharesBoughtAmount;
     private BigDecimal pricePerShare;
     private BigDecimal totalCost;
     private BigDecimal amountPaid;
     private BigDecimal amountRemaining;

    private long totalPages;
//    private String PharmacyUuid;
//    private String stockLocationName;

    long totalElements;
    int pageNumber;
}

