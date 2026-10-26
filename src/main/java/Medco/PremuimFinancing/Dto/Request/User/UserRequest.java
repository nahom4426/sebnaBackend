package Medco.PremuimFinancing.Dto.Request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UserRequest {

    @NotBlank
    @Size(min = 5, max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 2, max = 25)
    private String title;

    @NotBlank
    @Size(min = 2, max = 25)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 25)
    private String fatherName;

    @NotBlank
    @Size(min = 2, max = 25)
    private String grandFatherName;

    @NotBlank
    @Size(min = 1, max = 10)
    private String gender;

    @NotBlank
    @Size(min = 9, max = 13)
    private String mobilePhone;

    @NotNull
    private String roleUuid;

    @DecimalMin(value = "5", message = "Minimum shares to buy is 5")
    @Digits(integer = 19, fraction = 0, message = "sharesBoughtAmount must be a whole number")
    private BigDecimal sharesBoughtAmount;

    private BigDecimal amountPaid;

}
