package Medco.PremuimFinancing.Dto.Request.User;

import Medco.PremuimFinancing.Enums.User.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
    @Setter
    public class SignUpRequest {
//        @NotBlank(message = "can't be blank")
//        @Size(min = 5, max = 50)
        @Email(message = "please enter a valid email")
        private String email;

        @Size(min = 6, max = 20,message = "size should be between  6 and 20")
        private String password;

//        @NotBlank(message = "can't be blank")
//        @Size(min = 2, max = 10,message = "size should be between 2 and 10")
        private String title;

        @NotBlank(message = "can't be blank")
        @Size(min = 2, max = 25,message = "size should be between 2 and 25")
        private String firstName;

        @NotBlank(message = "can't be blank")
        @Size(min = 2, max = 25,message = "size should be between 2 and 25")
        private String fatherName;
//
//        @NotBlank(message = "can't be blank")
//        @Size(min = 2, max = 25,message = "size should be between 2 and 25")
        private String grandFatherName;

        @NotBlank(message = "can't be blank")
        @Size(min = 1, max = 10,message = "size should be between 2 and 25")
        private String gender;

        @NotBlank(message = "can't be blank")
        @Size(min = 9, max = 13,message = "size should be between 9 and 13")
        private String mobilePhone;

//        private Status userStatus;

        private UserType userType;

//        private String providerUuid;

        @NotBlank(message = "can't be blank")
        private String roleUuid;

//        private String branchUuid;

    }


