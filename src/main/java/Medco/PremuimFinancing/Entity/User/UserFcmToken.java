package Medco.PremuimFinancing.Entity.User;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_fcm_token")
public class UserFcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String quotationUuid = java.util.UUID.randomUUID().toString();

    private String fcm_token;
    private Date created_at ;
    private  Date last_used_at;
    private boolean is_active=true;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @JsonManagedReference
    private UserEntity user;

}
