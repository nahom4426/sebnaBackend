package Medco.PremuimFinancing.Dto.Response.User;

import Medco.PremuimFinancing.Enums.User.Status;
import Medco.PremuimFinancing.Enums.User.UserType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String userUuid;
    private String insuredUuid;
    private String institutionId;
    private String email;
    private String roleUuid;
    private String roleName;
    private String title;
    private String firstName;
    private String fatherName;
    private String grandFatherName;
    private String gender;
    private String mobilePhone;
    private Status userStatus;
    private UserType userType;
    private String profilePicture;

     private BigDecimal sharesBoughtAmount;
     private BigDecimal pricePerShare;
     private BigDecimal totalCost;
     private BigDecimal amountPaid;
     private BigDecimal amountRemaining;


    private List<String> privileges;

     public JwtResponse(String accessToken, String userUuid, String insuredUuid, String institutionId, String email, String roleUuid, String roleName, String title, String firstName, String fatherName,
                        String grandFatherName, String gender, String mobilePhone, Status userStatus, UserType userType,
                        String profilePicture, BigDecimal sharesBoughtAmount, BigDecimal amountPaid, BigDecimal amountRemaining, List<String> privileges) {
        this(accessToken, userUuid, insuredUuid, institutionId, email, roleUuid, roleName, title, firstName, fatherName,
                grandFatherName, gender, mobilePhone, userStatus, userType, profilePicture,
                sharesBoughtAmount, null, null, amountPaid, amountRemaining, privileges);
    }

     public JwtResponse(String accessToken, String userUuid, String insuredUuid, String institutionId, String email, String roleUuid, String roleName, String title, String firstName, String fatherName,
                        String grandFatherName, String gender, String mobilePhone, Status userStatus, UserType userType,
                        String profilePicture, BigDecimal sharesBoughtAmount, BigDecimal pricePerShare, BigDecimal totalCost, BigDecimal amountPaid, BigDecimal amountRemaining, List<String> privileges) {
        this.token = accessToken;
        this.userUuid = userUuid;
        this.insuredUuid = insuredUuid;
        this.institutionId = institutionId;
        this.email = email;
        this.roleUuid = roleUuid;
        this.roleName=roleName;
        this.title=title;
        this.firstName=firstName;
        this.fatherName=fatherName;
        this.grandFatherName=grandFatherName;
        this.gender=gender;
        this.mobilePhone=mobilePhone;
        this.userStatus=userStatus;
        this.userType=userType;
        this.profilePicture=profilePicture;

         this.sharesBoughtAmount = sharesBoughtAmount;
         this.pricePerShare = pricePerShare;
         this.totalCost = totalCost;
         this.amountPaid = amountPaid;
         this.amountRemaining = amountRemaining;


        this.privileges = privileges;

    }
}
