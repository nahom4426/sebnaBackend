package Medco.PremuimFinancing.Entity.Security;

import Medco.PremuimFinancing.utils.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "login_logs")
public class LoginLog extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 40)
    private String userUuid;

    @Column(nullable = false)
    private boolean success;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 250)
    private String userAgent;

    @Column(length = 250)
    private String failureReason;
}
