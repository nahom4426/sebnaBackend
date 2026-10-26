package Medco.PremuimFinancing.Entity.Messaging;

import Medco.PremuimFinancing.utils.Audit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "email_outbox")
public class EmailOutbox extends Audit {

    public enum Status {
        PENDING,
        SENT,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String toEmail;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String bodyHtml;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private int attempts = 0;

    private Instant nextAttemptAt;

    @Column(columnDefinition = "text")
    private String lastError;

    private String userUuid;

    private String emailType;
}
