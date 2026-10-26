package Medco.PremuimFinancing.Repository.Messaging;

import Medco.PremuimFinancing.Entity.Messaging.EmailOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, UUID> {

    @Query("""
            select e from EmailOutbox e
            where e.status in :statuses
              and (e.nextAttemptAt is null or e.nextAttemptAt <= :now)
            order by e.createdAt asc
            """)
    List<EmailOutbox> findReadyToSend(
            @Param("statuses") List<EmailOutbox.Status> statuses,
            @Param("now") Instant now,
            Pageable pageable
    );

    Optional<EmailOutbox> findTopByUserUuidAndEmailTypeOrderByCreatedAtDesc(String userUuid, String emailType);
}
