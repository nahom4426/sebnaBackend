package Medco.PremuimFinancing.Entity.User;

import Medco.PremuimFinancing.Entity.BankAccount;
import Medco.PremuimFinancing.Entity.UserRole.Role;
import Medco.PremuimFinancing.Enums.User.Status;
import Medco.PremuimFinancing.Enums.User.UserType;
import Medco.PremuimFinancing.utils.Audit;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "mobilePhone")
})
@Inheritance(strategy = InheritanceType.JOINED)
public class UserEntity extends Audit {

    @Serial
    private static final long serialVersionUID = -8809180659179591610L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull
//    private String roleUuid;

    @Size(min = 36, max = 40)
    private String userUuid = UUID.randomUUID().toString();

//    @Size(min = 5, max = 50)
    @Email
    private String email;

    @Size(min = 5, max = 120)
    private String password;

    private String otp;


//    @Size(min = 2, max = 25)
    private String title;

    @NotBlank
    @Size(min = 2, max = 25)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 25)
    private String fatherName;


    private String grandFatherName;

    @NotBlank
    @Size(min = 1, max = 10)
    private String gender;

    @NotBlank
    @Size(min = 9, max = 13)
    @Column(nullable = false, unique = true)
    private String mobilePhone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date BirthDate;

    @Enumerated(EnumType.STRING)
    private Status userStatus=Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    private UserType userType;


    private String passwordResetCode;
    private String emailVerificationToken;
    private String profilePicture;
    private String userName;

    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "bytea")
    private byte[] imageData;

    private Instant lastActiveAt;

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted=false;

    @Column(precision = 19, scale = 0)
    private BigDecimal sharesBoughtAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal purchasePricePerShare;

    @Column(precision = 19, scale = 2)
    private BigDecimal amountPaid;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @OneToOne(mappedBy = "client")
    @JsonBackReference
    private BankAccount bankAccount;

    @OneToOne(mappedBy = "user")
    @JsonBackReference
    private UserFcmToken userFcmToken;


}