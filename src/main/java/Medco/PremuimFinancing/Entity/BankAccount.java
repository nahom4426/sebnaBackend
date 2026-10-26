package Medco.PremuimFinancing.Entity;


import Medco.PremuimFinancing.Entity.User.UserEntity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String bankAccountUuid = UUID.randomUUID().toString();

    @Column(unique = true)
    @NotBlank(message = "Account number cannot be blank")
    @Pattern(regexp = "\\d{10,16}", message = "Account number must be between 10 to 16 digits")
    private String accountNumber;

    private double amountForQuotation;




    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @JsonManagedReference
    private UserEntity client;

}
