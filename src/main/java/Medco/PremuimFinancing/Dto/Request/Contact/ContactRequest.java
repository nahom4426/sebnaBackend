package Medco.PremuimFinancing.Dto.Request.Contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactRequest {

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 200)
    private String email;

    @Size(max = 30)
    private String phone;

    @Size(max = 100)
    private String subject;

    @NotBlank
    @Size(max = 5000)
    private String message;
}
