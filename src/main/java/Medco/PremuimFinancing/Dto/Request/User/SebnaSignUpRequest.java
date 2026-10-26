package Medco.PremuimFinancing.Dto.Request.User;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SebnaSignUpRequest {
    @Email(message = "please enter a valid email")
    private String email;

    private String title;

    @NotBlank(message = "can't be blank")
    @Size(min = 2, max = 25, message = "size should be between 2 and 25")
    private String firstName;

    @NotBlank(message = "can't be blank")
    @Size(min = 2, max = 25, message = "size should be between 2 and 25")
    private String fatherName;

    private String grandFatherName;

    @NotBlank(message = "can't be blank")
    @Size(min = 1, max = 10, message = "size should be between 1 and 10")
    private String gender;

    @NotBlank(message = "can't be blank")
    @Size(min = 9, max = 13, message = "size should be between 9 and 13")
    private String mobilePhone;

    @NotBlank(message = "can't be blank")
    private String roleUuid;

    @DecimalMin(value = "5", message = "Minimum shares to buy is 5")
    @Digits(integer = 19, fraction = 0, message = "sharesBoughtAmount must be a whole number")
    private BigDecimal sharesBoughtAmount;

    private BigDecimal amountPaid;
}
